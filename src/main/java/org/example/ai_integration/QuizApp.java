package org.example.ai_integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Objects;


public class QuizApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try { org.example.ai_integration.model.CreateSchema.initAll(); } catch (Exception ignored) {}

        Navigator.init(primaryStage, new javafx.scene.layout.StackPane(), 1200, 800);

        var user = org.example.ai_integration.model.UserManager
                .getInstance().getLoggedInUser();

        if (user == null) {
            Navigator.toLogin();
            primaryStage.setTitle("Login");
        } else {
            Navigator.toQuizLibrary();
            primaryStage.setTitle("Quiz Library");
        }
        primaryStage.show();
    }
}
