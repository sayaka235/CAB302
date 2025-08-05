package org.example.ai_integration;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class QuizApp extends Application {

    @Override
    public void start(Stage stage) {
        // Input and output areas

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        // File upload button
        Button uploadButton = new Button("Upload File");
        uploadButton.setOnAction(e -> {

            FileChooser fileChooser = new FileChooser();

        // Create filters
            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
            FileChooser.ExtensionFilter docxFilter = new FileChooser.ExtensionFilter("Word Documents", "*.docx");

        // Add them
            fileChooser.getExtensionFilters().addAll(txtFilter, pdfFilter, docxFilter);

        // Set default to PDF
            fileChooser.setSelectedExtensionFilter(pdfFilter);


            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    String content = FileUtil.readFileContent(selectedFile);
                    outputArea.setText("Generating quiz...");
                    new Thread(() -> {
                        String quiz = QuizAPI.generateQuiz(content);
                        Platform.runLater(() -> outputArea.setText(quiz));
                    }).start();
                } catch (IOException ex) {
                    outputArea.setText("Failed to read file: " + ex.getMessage());
                }
            }
        });

        VBox layout = new VBox(10, uploadButton, outputArea);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 500, 400));
        stage.setTitle("AI Quiz Generator (Gemini)");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
