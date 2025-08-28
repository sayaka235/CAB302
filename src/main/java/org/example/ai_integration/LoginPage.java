package org.example.ai_integration;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.CreateSchema;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HexFormat;

public class LoginPage extends Application {
    private TextField emailField;
    private PasswordField passField;
    private Button loginButton;

    @Override
    public void start(Stage stage) {

        // 2) Build minimal login UI
        emailField = new TextField();
        emailField.setPromptText("you@example.com");

        passField = new PasswordField();
        passField.setPromptText("Password");

        loginButton = new Button("Log in");

        VBox root = new VBox(12,
                new Label("Email"), emailField,
                new Label("Password"), passField,
                loginButton
        );
        root.setPadding(new Insets(16));

        // 3) Click handler: authenticate
        loginButton.setOnAction(evt -> {
            String email = emailField.getText().trim();
            String pwd   = passField.getText();

            if (email.isEmpty() || pwd.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing fields", "Please enter both email and password.");
                return;
            }

            String hash = sha256Hex(pwd); // NOTE: consider salted hashes in real apps
            Long userId = findUserIdByCredentials(email, hash);

            if (userId != null) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Logged in! User ID: " + userId);
                // TODO: navigate to your main app scene
            } else {
                showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid email or password.");
            }
        });

        stage.setScene(new Scene(root, 380, 240));
        stage.setTitle("Log in");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }

    /**
     * Returns the user's id if (email, password_hash) matches; otherwise null.
     */
    private Long findUserIdByCredentials(String email, String passwordHash) {
        // Ensure your Users table has columns: id (INTEGER PK), email (TEXT UNIQUE), password_hash (TEXT)
        String sql = "SELECT id FROM Users WHERE email = ? AND password_hash = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        } catch (SQLException e) {
            // In production, log properly
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database error", e.getMessage());
        }
        return null;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}
