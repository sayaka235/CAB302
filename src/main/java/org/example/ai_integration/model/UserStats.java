package org.example.ai_integration.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserStats extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ai_integration/UserStatsScene.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Your Statistics");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
