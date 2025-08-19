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

    private String uploadedContent = null;

    @Override
    public void start(Stage stage) {
        // Output area
        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);

        // Dropdown for quiz type
        ComboBox<String> quizTypeBox = new ComboBox<>();
        quizTypeBox.getItems().addAll("Multiple Choice", "True/False", "Fill in the Blank");
        quizTypeBox.setPromptText("Select Quiz Type");

        // File upload button
        Button uploadButton = new Button("Upload File");
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                    new FileChooser.ExtensionFilter("Word Documents", "*.docx")
            );
            fileChooser.setSelectedExtensionFilter(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                try {
                    uploadedContent = FileUtil.readFileContent(selectedFile);
                    outputArea.setText("File uploaded. Now choose quiz type and click Start Quiz.");
                } catch (IOException ex) {
                    outputArea.setText("Failed to read file: " + ex.getMessage());
                }
            }
        });

        // Start Quiz button
        Button startQuizButton = new Button("Start Quiz");
        startQuizButton.setOnAction(e -> {
            String quizType = quizTypeBox.getValue();

            if (uploadedContent == null) {
                outputArea.setText("Please upload a file first.");
            } else if (quizType == null || quizType.isEmpty()) {
                outputArea.setText("Please select a quiz type.");
            } else {
                outputArea.setText("Generating " + quizType + " quiz...");
                new Thread(() -> {
                    String quiz = QuizAPI.generateQuiz(uploadedContent, quizType);
                    Platform.runLater(() -> outputArea.setText(quiz));
                }).start();
            }
        });

        VBox layout = new VBox(10,
                uploadButton,
                quizTypeBox,
                startQuizButton,
                outputArea
        );
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 600, 500));
        stage.setTitle("AI Quiz Generator (Gemini)");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
