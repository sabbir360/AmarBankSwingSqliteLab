package com.amarbank.db;

import com.amarbank.model.Account;
import com.amarbank.model.AccountSchema;
import com.amarbank.model.FieldDescriptor;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Single SQLite persistence class for the whole app. Owns the connection, the
 * {@code accounts} and {@code users} tables, and all CRUD. Extension fields
 * from {@link AccountSchema} are added to the {@code accounts} table and bound
 * automatically, so a new field needs no change here.
 */
public class BankDatabase {

    private static final String DB_DIR = "db_file";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + "/amarbank.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "SQLite JDBC driver not found. Run ./run.sh (or run.bat on Windows) "
                            + "to download the driver into lib/ and launch the app.", e);
        }
    }

    private Connection connect() throws SQLException {
        new File(DB_DIR).mkdirs();
        return DriverManager.getConnection(DB_URL);
    }

    /** Creates both tables (if missing) and seeds the default admin login. */
    public void createTables() {
        String accounts = "CREATE TABLE IF NOT EXISTS accounts ("
                + "account_number TEXT PRIMARY KEY,"
                + "type TEXT NOT NULL,"
                + "holder_name TEXT NOT NULL,"
                + "balance REAL NOT NULL,"
                + "special REAL NOT NULL)";
        String users = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT PRIMARY KEY,"
                + "password TEXT NOT NULL)";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(accounts);
            stmt.execute(users);
            ensureExtensionColumns(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables: " + e.getMessage(), e);
        }
        seedAdmin();
    }

    /** Adds any {@link AccountSchema} extension column missing from {@code accounts}. */
    private void ensureExtensionColumns(Connection conn) throws SQLException {
        Set<String> existing = new HashSet<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(accounts)")) {
            while (rs.next()) {
                existing.add(rs.getString("name"));
            }
        }
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            if (!existing.contains(field.column())) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE accounts ADD COLUMN "
                            + field.column() + " " + field.sqlType().name());
                }
            }
        }
    }

    private void seedAdmin() {
        String sql = "INSERT OR IGNORE INTO users (username, password) VALUES ('admin', 'admin123')";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to seed admin user: " + e.getMessage(), e);
        }
    }

    /** True when the username/password pair matches a stored user. */
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login check failed: " + e.getMessage(), e);
        }
    }

    public void insertAccount(Account account) {
        StringBuilder columns = new StringBuilder("account_number, type, holder_name, balance, special");
        StringBuilder placeholders = new StringBuilder("?,?,?,?,?");
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            columns.append(", ").append(field.column());
            placeholders.append(",?");
        }
        String sql = "INSERT INTO accounts (" + columns + ") VALUES (" + placeholders + ")";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindAccount(ps, account);
            bindExtensions(ps, account, 6);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert account: " + e.getMessage(), e);
        }
    }

    public void updateAccount(Account account) {
        StringBuilder sets = new StringBuilder("type = ?, holder_name = ?, balance = ?, special = ?");
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            sets.append(", ").append(field.column()).append(" = ?");
        }
        String sql = "UPDATE accounts SET " + sets + " WHERE account_number = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountType());
            ps.setString(2, account.getAccountHolderName());
            ps.setDouble(3, account.getBalance());
            ps.setDouble(4, account.getSpecialAttribute());
            int next = bindExtensions(ps, account, 5);
            ps.setString(next, account.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account: " + e.getMessage(), e);
        }
    }

    public void deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete account: " + e.getMessage(), e);
        }
    }

    /** Loads every stored account, rebuilding the correct subclass. */
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Account account = Account.create(
                        rs.getString("type"),
                        rs.getString("account_number"),
                        rs.getString("holder_name"),
                        rs.getDouble("balance"),
                        rs.getDouble("special"));
                for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
                    field.apply(account, rs.getString(field.column()));
                }
                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load accounts: " + e.getMessage(), e);
        }
        return accounts;
    }

    private void bindAccount(PreparedStatement ps, Account account) throws SQLException {
        ps.setString(1, account.getAccountNumber());
        ps.setString(2, account.getAccountType());
        ps.setString(3, account.getAccountHolderName());
        ps.setDouble(4, account.getBalance());
        ps.setDouble(5, account.getSpecialAttribute());
    }

    /** Binds each extension field starting at {@code startIndex}; returns the next free index. */
    private int bindExtensions(PreparedStatement ps, Account account, int startIndex) throws SQLException {
        int index = startIndex;
        for (FieldDescriptor field : AccountSchema.EXTENSION_FIELDS) {
            Object value = field.read(account);
            if (field.sqlType() == FieldDescriptor.SqlType.REAL) {
                ps.setDouble(index, value == null ? 0.0 : ((Number) value).doubleValue());
            } else {
                ps.setString(index, value == null ? null : value.toString());
            }
            index++;
        }
        return index;
    }
}
