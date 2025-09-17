package org.example.ai_integration.model;

import java.sql.*;
import java.time.LocalDate;

public class Signup {
    public void insertUser(User newUser) throws SQLException {
        String email = newUser.getEmail();
        String first = newUser.getFirst();
        String last = newUser.getLast();
        LocalDate dob = newUser.getDob();
        String password = newUser.getPassword();

        if (email.isEmpty() || first.isEmpty() || last.isEmpty() || dob == null || password.isEmpty()) {
            throw new SQLException("Missing fields", "Complete all fields.");
        }

        if(!(first.matches("^[A-Za-z-]+$"))){
            throw new SQLException("Firstname is invalid");
        }

        String sql = "INSERT INTO Users (email, firstName, lastName, dob, passwordHash) VALUES (?,?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, first);
            ps.setString(3, last);
            ps.setString(4, dob.toString());
            ps.setString(5, password);
            ps.executeUpdate();
        }
    }
}
