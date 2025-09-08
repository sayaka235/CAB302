package org.example.ai_integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var url  = App.class.getResource("/org/example/ai_integration/ApplicationEntryScene.fxml");
        Parent root = FXMLLoader.load(Objects.requireNonNull(url, "ApplicationEntryScene.fxml not found"));

        // 👇 THIS is the critical line you’re missing
        Navigator.init(stage, root, 480, 360);

        stage.setTitle("Sign Up");
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
