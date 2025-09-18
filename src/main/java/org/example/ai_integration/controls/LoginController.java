package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.User;
import org.example.ai_integration.model.UserManager;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HexFormat;
/**
 * Controller for the application's login page.
 * <p>
 * Provides navigation and login logic for {@code LoginScene.fxml}.
 */
public class LoginController {
    /** Field to hold the entered email. */
    @FXML private TextField emailField;

    /** Field to hold the entered password. */
    @FXML private PasswordField passwordField;

    /**
     * Handles the login process.
     * <p>
     * Verifies that the entered email and password match a user in the database.
     * If successful, creates a {@link User} object and navigates to the dashboard.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String pass  = passwordField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Missing fields", "Please enter email and password.");
            return;
        }

        String sql = "SELECT id, firstName, lastName, dob FROM Users WHERE email = ? AND passwordHash = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, sha256(pass));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String dbFirstname = rs.getString("firstName");
                    String dbLastname = rs.getString("lastName");
                    //String dbEmail = rs.getString("email");
                    //String dbPasswordHash = rs.getString("passwordHash");
                    LocalDate dbDob = LocalDate.parse(rs.getString("dob"));
                    String dbuserID  = rs.getString("id");

                    User loggedInUser = new User(email, dbFirstname, dbLastname, dbDob, sha256(pass), dbuserID);
                    UserManager.getInstance().setLoggedInUser(loggedInUser);
                    alert(Alert.AlertType.INFORMATION, "Welcome",  "Hello " + loggedInUser.getName()+ ", login successful." );
                    Navigator.toDashboard();
                } else {
                    alert(Alert.AlertType.ERROR, "Login failed", "Invalid email or password.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Database error", e.getMessage());
        }
    }
    /**
     * Goes to the sign-up page if the "Go to Signup" button is pressed.
     */
    @FXML
    private void goSignup() {
        try { Navigator.toSignup(); }
        catch (Exception e) { e.printStackTrace(); alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage()); }
    }

    /**
     * Hashes the entered password using SHA-256.
     *
     * @param s the string to hash
     * @return the SHA-256 hash of the string
     */
    private static String sha256(String s) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    /**
     * Creates an alert message to show warnings or errors.
     *
     * @param t     the type of alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert window
     * @param msg   the content message of the alert
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
