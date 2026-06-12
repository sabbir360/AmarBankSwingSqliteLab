package com.amarbank.db;

import com.amarbank.model.Account;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Single SQLite persistence class for the whole app (HR-reference style).
 * Owns the connection, the {@code accounts} and {@code users} tables, and all
 * CRUD. To add a new persisted thing, add one method here.
 *
 * To export to CSV instead (old assignment format), iterate
 * {@link #getAllAccounts()} and write {@link Account#toCsvRow()} per line.
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
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables: " + e.getMessage(), e);
        }
        seedAdmin();
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
        String sql = "INSERT INTO accounts (account_number, type, holder_name, balance, special) "
                + "VALUES (?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            bindAccount(ps, account);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert account: " + e.getMessage(), e);
        }
    }

    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET type = ?, holder_name = ?, balance = ?, special = ? "
                + "WHERE account_number = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, account.getAccountType());
            ps.setString(2, account.getAccountHolderName());
            ps.setDouble(3, account.getBalance());
            ps.setDouble(4, account.getSpecialAttribute());
            ps.setString(5, account.getAccountNumber());
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
        String sql = "SELECT account_number, type, holder_name, balance, special FROM accounts";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                accounts.add(Account.create(
                        rs.getString("type"),
                        rs.getString("account_number"),
                        rs.getString("holder_name"),
                        rs.getDouble("balance"),
                        rs.getDouble("special")));
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
}
