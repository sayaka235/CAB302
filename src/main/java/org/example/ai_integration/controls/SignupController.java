package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Database;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.HexFormat;

public class SignupController {
    @FXML private TextField emailField, firstNameField, lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private PasswordField passwordField;

    @FXML private void handleSignup() {
        try {
            String email = emailField.getText().trim();
            String first = firstNameField.getText().trim();
            String last  = lastNameField.getText().trim();
            LocalDate dob= dobPicker.getValue();
            String pass  = passwordField.getText();

            if (email.isEmpty() || first.isEmpty() || last.isEmpty() || dob == null || pass.isEmpty()) {
                alert(Alert.AlertType.WARNING,"Missing fields","Complete all fields."); return;
            }

            String sql = "INSERT INTO Users(email, firstName, lastName, dob, passwordHash) VALUES(?,?,?,?,?)";
            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, first);
                ps.setString(3, last);
                ps.setString(4, dob.toString());
                ps.setString(5, sha256(pass));
                ps.executeUpdate();
            }
            alert(Alert.AlertType.INFORMATION,"Success","Account created. You can log in now.");
            Navigator.toLogin();
        } catch (Exception e) { e.printStackTrace(); alert(Alert.AlertType.ERROR,"Signup failed",e.getMessage()); }
    }

    @FXML private void goLogin() { try { Navigator.toLogin(); } catch (Exception e) { e.printStackTrace(); } }

    private static String sha256(String s) {
        try { var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private static void alert(Alert.AlertType t, String h, String c){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(c); a.showAndWait(); }
}
