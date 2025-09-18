package org.example.ai_integration.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * Launches the User Statistics page of the application.
 * <p>
 * This class loads {@code UserStatsScene.fxml} and displays
 * the user statistics screen in a new window.
 */
public class UserStats extends Application {
    /**
     * Starts the JavaFX application.
     * <p>
     * Loads the FXML file, sets the title, and shows the scene.
     * @param stage the primary stage for the User Statistics window
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/ai_integration/UserStatsScene.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Your Statistics");
        stage.setScene(scene);
        stage.show();
    }
    /**
     * The main entry point of the program.
     * <p>
     * Launches the User Statistics JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
