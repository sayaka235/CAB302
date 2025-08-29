package org.example.ai_integration;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationEntryPoint extends Application
{
    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException
    {
        // Load the FXML file
        Parent root = FXMLLoader.load(getClass().getResource("ApplicationEntryScene.fxml"));

        // Create a new Scene with the loaded FXML content
        Scene applicationEntryScene = new Scene(root, 400, 300); // Set desired width and height

        // Set the scene on the primary stage
        primaryStage.setTitle("Application Entry");
        primaryStage.setScene(applicationEntryScene);
        primaryStage.show();
    }
}