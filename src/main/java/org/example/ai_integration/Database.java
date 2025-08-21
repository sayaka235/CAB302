package org.example.ai_integration;

import java.nio.file.*;
import java.sql.*;

public final class Database {
    private static final Path DB_DIR  = Paths.get("data").toAbsolutePath();
    private static final Path DB_PATH = DB_DIR.resolve("Database.sqlite");

    private Database() {}

    public static Connection getConnection() throws SQLException {
        try { Class.forName("org.sqlite.JDBC"); } catch (ClassNotFoundException ignored) {}
        try { Files.createDirectories(DB_DIR); }
        catch (java.io.IOException e) { throw new RuntimeException("Cannot create DB dir: " + DB_DIR, e); }

        String url = "jdbc:sqlite:" + DB_PATH;
        Connection conn = DriverManager.getConnection(url);
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            s.execute("PRAGMA busy_timeout = 3000");
            // s.execute("PRAGMA journal_mode = WAL"); // optional
        }
        return conn;
    }
}
