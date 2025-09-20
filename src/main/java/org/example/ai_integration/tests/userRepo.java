package org.example.ai_integration.tests;

import org.example.ai_integration.model.User;
import java.sql.*;

public class userRepo {
    public void insert(Connection c, User u) throws SQLException {
        final String sql =
                "INSERT INTO Users (email, firstName, lastName, dob, passwordHash) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getFirst());
            ps.setString(3, u.getLast());
            ps.setString(4, u.getDob().toString());
            ps.setString(5, u.getPassword());
            ps.executeUpdate();
        }
    }
}
