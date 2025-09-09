package org.example.ai_integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class ChartModel extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL fxml = getClass().getResource("/org/example/ai_integration/chart-view.fxml");
        if (fxml == null)
            throw new RuntimeException("FXML file not found: chart-view.fxml");

        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);

        // Optional: add CSS if you have one
        URL css = getClass().getResource("/org/example/ai_integration/chart.css");
        if (css != null) {
            scene.getStylesheets().add(Objects.requireNonNull(css).toExternalForm());
        }

        stage.setScene(scene);
        stage.setTitle("Performance Chart");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
