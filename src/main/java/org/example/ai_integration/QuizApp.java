package org.example.ai_integration;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the Quiz application.
 * <p>
 * Initializes the primary stage, sets up navigation,
 * and decides whether to show the login screen or quiz library
 * depending on whether a user is logged in.
 */
public class QuizApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Removed CreateSchema.initAll() — schema is now managed externally

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
