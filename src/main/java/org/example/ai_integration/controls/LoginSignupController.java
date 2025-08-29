/*package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Database;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HexFormat;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = passwordField.getText();
        if (email.isEmpty() || pass.isEmpty()) { alert(Alert.AlertType.WARNING,"Missing","Enter email & password."); return; }

        String sql = "SELECT id FROM Users WHERE email=? AND passwordHash=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, sha256(pass));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) alert(Alert.AlertType.INFORMATION,"Welcome","Login OK. User ID: "+rs.getLong("id"));
                else alert(Alert.AlertType.ERROR,"Login failed","Invalid email or password.");
            }
        } catch (Exception e) { e.printStackTrace(); alert(Alert.AlertType.ERROR,"DB error", e.getMessage()); }
    }

    @FXML private void goSignup() { try { Navigator.toSignup(); } catch (Exception e) { e.printStackTrace(); } }

    private static String sha256(String s) {
        try { var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private static void alert(Alert.AlertType t, String h, String c){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(c); a.showAndWait(); }
}
*/