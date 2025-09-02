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
    public void start(Stage stage) throws Exception{
        URL fxml = getClass().getResource("/org/example/ai_integration/quiz-view.fxml");
        if (fxml == null)
            throw new RuntimeException("FXML file not found: quiz-view.fxml");

        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/example/ai_integration/quiz.css")).toExternalForm());
        stage.setScene(scene);
        stage.setTitle("AI Quiz Generator");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
