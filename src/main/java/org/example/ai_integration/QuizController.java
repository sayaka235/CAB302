package org.example.ai_integration;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class QuizController {
    @FXML private Button uploadButton;
    @FXML private ComboBox<String> quizTypeBox;
    @FXML private Button startQuizButton;
    @FXML private TextArea outputArea;

    private String uploadedContent = null;

    @FXML
    private void initialize() {
        // Populate quiz types
        quizTypeBox.getItems().addAll("Multiple Choice", "True/False", "Fill in the Blank");

        // File Upload
        uploadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files", "*.txt");
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
            FileChooser.ExtensionFilter docxFilter = new FileChooser.ExtensionFilter("Word Documents", "*.docx");
            fileChooser.getExtensionFilters().addAll(txtFilter, pdfFilter, docxFilter);
            fileChooser.setSelectedExtensionFilter(pdfFilter);

            File selectedFile = fileChooser.showOpenDialog(getStage());
            if (selectedFile != null) {
                try {
                    uploadedContent = FileUtil.readFileContent(selectedFile);
                    outputArea.setText("File uploaded. Now choose quiz type and click Start Quiz.");
                } catch (IOException ex) {
                    outputArea.setText("Failed to read file: " + ex.getMessage());
                }
            }
        });

        // Start Quiz
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
    }

    private Stage getStage() {
        return (Stage) outputArea.getScene().getWindow();
    }
}
