package org.example.ai_integration;

import org.example.ai_integration.model.Database;

import java.sql.*;

public class NoteSummaryRepo {
    public static long createNotes(String notes, String title, long userId) throws SQLException {
        try (Connection c = Database.getConnection()) {
            // Insert quiz metadata (with title & imagePath)
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO noteSummary (notes, title, userID) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, notes);
                ps.setString(2, title);
                ps.setLong(3, userId);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Failed to create notes");
                    long notesId = rs.getLong(1);
                    return notesId;
                }
            }
        }
    }
}
