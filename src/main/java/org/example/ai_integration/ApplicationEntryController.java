package org.example.ai_integration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;

public class ApplicationEntryController
{
    @FXML
    private Button loginButton;

    @FXML
    private void handleLoginAction(ActionEvent event) {
        try {
            // Load the FXML for the target scene (Scene2.fxml)
            Parent newRoot = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));

            // Get the current scene and set the new root
            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(newRoot);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message)
        }
    }

    @FXML
    private void handleSignUpAction(ActionEvent event)
    {

    }
}
