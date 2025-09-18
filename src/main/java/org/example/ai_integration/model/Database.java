package org.example.ai_integration.model;

import java.nio.file.*;
import java.sql.*;

/**
 * Utility class for handling database connections to the SQLite database.
 * <p>
 * Ensures that the database file and directory exist, and applies useful
 * settings such as enabling foreign keys and busy timeout.
 */
public final class Database {

    /** The directory where the SQLite database is stored */
    private static final Path DB_DIR  = Paths.get("data").toAbsolutePath();
    /** The full path to the SQLite database file */
    private static final Path DB_PATH = DB_DIR.resolve("Database.sqlite");

    /** Private constructor to prevent instantiation of this utility class */
    private Database() {}

    /**
     * Gets a new database connection to the SQLite file.
     * <p>
     * Creates the database directory if it does not exist. Also configures
     * SQLite settings such as enabling foreign keys and setting a busy timeout.
     *
     * @return a {@link Connection} to the SQLite database
     * @throws SQLException if the database connection cannot be established
     */
    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {}

        try {
            Files.createDirectories(DB_DIR);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Cannot create DB dir: " + DB_DIR, e);
        }

        String url = "jdbc:sqlite:" + DB_PATH;
        Connection conn = DriverManager.getConnection(url);

        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");     // Enforce foreign key constraints
            s.execute("PRAGMA busy_timeout = 3000");   // Wait up to 3 seconds if DB is locked
        }
        return DriverManager.getConnection(url);
    }
}
