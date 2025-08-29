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
    private static Stage stage; // optional: to update title

    public static void init(Stage s, Parent initialRoot, double w, double h) {
        stage = s;
        scene = new Scene(initialRoot, w, h);
        stage.setScene(scene);
    }

    public static void toLogin() throws IOException {
        ensureInitialized();
        scene.setRoot(load(LOGIN_FXML));
        if (stage != null) stage.setTitle("Login");
    }

    public static void toSignup() throws IOException {
        ensureInitialized();
        scene.setRoot(load(SIGNUP_FXML));
        if (stage != null) stage.setTitle("Sign Up");
    }

    private static Parent load(String path) throws IOException {
        var url = Navigator.class.getResource(path);
        return FXMLLoader.load(Objects.requireNonNull(url, "Missing FXML: " + path));
    }

    private static void ensureInitialized() {
        if (scene == null) {
            throw new IllegalStateException("Navigator not initialized. Call Navigator.init(...) in App.start().");
        }
    }
}
