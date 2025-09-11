package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Signup;
import org.example.ai_integration.model.User;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.HexFormat;

public class SignupController {
    @FXML private TextField emailField, firstNameField, lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private CheckBox showPasswordCheckBox;


    private Signup signup;

    public SignupController() {
        this.signup = new Signup();
    }

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

    @FXML private void goLogin() { try { Navigator.toLogin(); } catch (Exception e) { e.printStackTrace(); } }

    private static String sha256(String s) {
        try { var md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private static void alert(Alert.AlertType t, String h, String c){ var a=new Alert(t); a.setHeaderText(h); a.setContentText(c); a.showAndWait(); }


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
