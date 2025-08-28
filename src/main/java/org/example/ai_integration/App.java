package org.example.ai_integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.ai_integration.model.CreateSchema;

import java.net.URL;
import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Ensure DB schema exists
        CreateSchema.initAll();

        // Start on SIGNUP page (switch to Login by clicking the button)
        URL url = App.class.getResource("/org/example/ai_integration/ApplicationEntryScene.fxml");
        var root = FXMLLoader.load(Objects.requireNonNull(url, "ApplicationEntryScene.fxml not found"));

        // Initialize Navigator with the first scene
        Navigator.init(stage, root, 480, 360);
        stage.setTitle("Sign Up");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
