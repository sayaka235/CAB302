package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Signup;
import org.example.ai_integration.model.User;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;
/**
 * Controller for the signup page of the application.
 * <p>
 * Handles new user registration by collecting user input,
 * hashing the password, and storing the new account in the database.
 * Connected to {@code SignupScene.fxml}.
 */
public class SignupController {
    /** Field for entering the user’s email */
    @FXML private TextField emailField;
    /** Field for entering the user’s first name */
    @FXML private TextField firstNameField;
    /** Field for entering the user’s last name */
    @FXML private TextField lastNameField;
    /** Date picker for selecting the user’s date of birth */
    @FXML private DatePicker dobPicker;
    /** Hidden password field (masked input) */
    @FXML private PasswordField passwordField;
    /** Visible password field (plain text input when "show password" is checked) */
    @FXML private TextField passwordTextField;
    /** Checkbox that toggles password visibility */
    @FXML private CheckBox showPasswordCheckBox;

    /** Helper object that manages signup database operations */
    private Signup signup;
    /** Creates the signup controller and initializes the signup helper */
    public SignupController() {
        this.signup = new Signup();
    }
    /**
     * Handles the signup process when the "Sign Up" button is clicked.
     * <p>
     * Collects all user input, hashes the password, creates a new {@code User} object,
     * and inserts it into the database. Shows a success message on completion.
     */
    @FXML private void handleSignup() {
        try {
            String email = emailField.getText().trim();
            String first = firstNameField.getText().trim();
            String last  = lastNameField.getText().trim();
            LocalDate dob= dobPicker.getValue();
            String pass  = sha256(passwordField.getText());

            User newUser = new User(email, first, last, dob, pass);
            signup.insertUser(newUser);
            alert(Alert.AlertType.INFORMATION,"Success","Account created. You can log in now.");
            Navigator.toLogin();
        } catch (Exception e) { e.printStackTrace(); alert(Alert.AlertType.ERROR,"Signup failed",e.getMessage()); }
    }
    /**
     * Navigates back to the login page if the "Go to Login" button is clicked.
     */
    @FXML private void goLogin() { try { Navigator.toLogin(); } catch (Exception e) { e.printStackTrace(); } }
    /**
     * Hashes the given string input using SHA-256.
     * <p>
     * Used to securely store passwords in the database.
     * @param s the plain text string to hash
     * @return the hashed string in hex format
     */
    private static String sha256(String s) {
        try { var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    /**
     * Creates and shows an alert popup with the given information.
     * @param t the type of alert (e.g., ERROR, INFORMATION)
     * @param h the header text of the alert
     * @param c the content message of the alert
     */
    private static void alert(Alert.AlertType t, String h, String c){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(c); a.showAndWait(); }


    /**
     * Initializes the signup form.
     * <p>
     * Ensures the plain text and hidden password fields are synced,
     * and sets up the checkbox to toggle password visibility.
     */
    @FXML
    public void initialize() {
        // Sync both fields
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // Toggle visibility
        showPasswordCheckBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                passwordTextField.setVisible(true);
                passwordTextField.setManaged(true);
                passwordField.setVisible(false);
                passwordField.setManaged(false);
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                passwordTextField.setVisible(false);
                passwordTextField.setManaged(false);
            }
        });
    }
}
