package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * Controller for the application entry screen that provides navigation for signing up.
 * This class is connected to {@code ApplicationEntryScene.fxml}.
 */
public class ApplicationEntryController
{
    /**
     * The button that triggers the login action.
     */
    @FXML
    private Button loginButton;
    /**
     * Handles the action when the "Login" button is clicked.
     * <p>
     * Loads the {@code LoginScene.fxml} file and replaces the current scene's root with it.
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void handleLoginAction(ActionEvent event) {
        try {
            // Load the FXML for the target scene (LoginScene.fxml)
            Parent newRoot = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));

            // Get the current scene and set the new root
            Scene currentScene = loginButton.getScene();
            currentScene.setRoot(newRoot);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message)
        }
    }

    /**
     * Handles the action when the 'Sign Up' button is clicked.
     * @param event the action event triggered by the button click.
     */
    @FXML
    private void handleSignUpAction(ActionEvent event)
    { }
}
