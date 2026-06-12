# SQLite (JDBC) Cheat Sheet

Mirrors `BankDatabase.java`. Requires the SQLite JDBC jar on the classpath
(`run.sh` downloads it automatically).

```
import java.sql.*;
```

## Connect

```
private static final String URL = "jdbc:sqlite:db_file/amarbank.db";

static {                                   // load driver once
    try { Class.forName("org.sqlite.JDBC"); }
    catch (ClassNotFoundException e) { throw new RuntimeException(e); }
}

private Connection connect() throws SQLException {
    new java.io.File("db_file").mkdirs();  // ensure folder exists
    return DriverManager.getConnection(URL);
}
```

## Create table

```
String sql = "CREATE TABLE IF NOT EXISTS accounts ("
        + "account_number TEXT PRIMARY KEY,"
        + "type TEXT NOT NULL,"
        + "holder_name TEXT NOT NULL,"
        + "balance REAL NOT NULL,"
        + "special REAL NOT NULL)";
try (Connection conn = connect();
     Statement stmt = conn.createStatement()) {
    stmt.execute(sql);
}
```

## Insert (PreparedStatement prevents SQL injection)

```
String sql = "INSERT INTO accounts VALUES (?,?,?,?,?)";
try (Connection conn = connect();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, "AB000001");
    ps.setString(2, "SAVINGS");
    ps.setString(3, "Jane Doe");
    ps.setDouble(4, 1000.0);
    ps.setDouble(5, 2.5);
    ps.executeUpdate();
}
```

`INSERT OR IGNORE` skips rows that would violate the primary key (used to seed
the default admin user once).

## Select

```
String sql = "SELECT * FROM accounts";
try (Connection conn = connect();
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(sql)) {
    while (rs.next()) {
        String number = rs.getString("account_number");
        double balance = rs.getDouble("balance");
    }
}

// Select with a condition + check existence
String q = "SELECT 1 FROM users WHERE username = ? AND password = ?";
try (Connection conn = connect();
     PreparedStatement ps = conn.prepareStatement(q)) {
    ps.setString(1, user);
    ps.setString(2, pass);
    try (ResultSet rs = ps.executeQuery()) {
        boolean valid = rs.next();   // true if a row matched
    }
}
```

## Update / Delete

```
String upd = "UPDATE accounts SET balance = ? WHERE account_number = ?";
try (Connection conn = connect();
     PreparedStatement ps = conn.prepareStatement(upd)) {
    ps.setDouble(1, 1500.0);
    ps.setString(2, "AB000001");
    ps.executeUpdate();
}

String del = "DELETE FROM accounts WHERE account_number = ?";
try (Connection conn = connect();
     PreparedStatement ps = conn.prepareStatement(del)) {
    ps.setString(1, "AB000001");
    ps.executeUpdate();
}
```

## Notes

- `try (... )` (try-with-resources) auto-closes `Connection`, `Statement`,
  `ResultSet` even on error.
- SQLite types: `TEXT`, `INTEGER`, `REAL`. Read with
  `rs.getString/getInt/getDouble`.
- For many inserts at once, use `conn.setAutoCommit(false)`,
  `ps.addBatch()` / `ps.executeBatch()`, then `conn.commit()`.
