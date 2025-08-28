package org.example.ai_integration;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public final class Navigator {
    private Navigator() {}

    private static final String LOGIN_FXML  = "/org/example/ai_integration/LoginScene.fxml";
    private static final String SIGNUP_FXML = "/org/example/ai_integration/ApplicationEntryScene.fxml";

    private static Scene scene;

    public static void init(Stage stage, Parent initialRoot, double w, double h) {
        scene = new Scene(initialRoot, w, h);
        stage.setScene(scene);
    }

    public static void toLogin() throws IOException {
        scene.setRoot(load(LOGIN_FXML));
    }

    public static void toSignup() throws IOException {
        scene.setRoot(load(SIGNUP_FXML));
    }

    private static Parent load(String path) throws IOException {
        var url = Navigator.class.getResource(path);
        return FXMLLoader.load(Objects.requireNonNull(url, "Missing FXML: " + path));
    }
}
