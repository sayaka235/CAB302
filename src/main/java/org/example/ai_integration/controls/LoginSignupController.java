package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginSignupController {

    @FXML
    private Button loginButton; // fx:id must be "loginButton" in the Signup FXML

    @FXML
    public void onLoginButtonClick(ActionEvent event) throws IOException {
        // Use the injected button to get the Stage
        Stage stage = (Stage) loginButton.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/ai_integration/login-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 480, 320);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}
