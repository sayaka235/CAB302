package org.example.ai_integration.model;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import java.util.*;

public class QuizController {

    @FXML private StackPane rootStack;
    @FXML private VBox uploadCard, quizCard, historyCard, dropZone;
    @FXML private Label uploadHint;
    @FXML private Button uploadButton, startQuizButton;
    @FXML private Label questionCounterLabel, percentCompleteLabel, questionLabel;
    @FXML private ProgressBar progressBar;
    @FXML private VBox optionsBox;
    @FXML private Button backButton, nextButton;
    @FXML private Button takeNewQuizButton;
    @FXML private ListView<QuizDao.AttemptRow> attemptsList;
    @FXML private Button retakeButton;
    @FXML private TextArea outputArea;

    private String uploadedContent = null;
    private long quizId;
    private long scoreId;
    private int currentIndex = 0;

    private List<QuizDao.McqQuestion> mcqQuestions = new ArrayList<>();

    private final Map<Long, Integer> selectedByQuestionId = new HashMap<>();

    private static final long CURRENT_USER_ID = 1L;

    @FXML
    private void initialize() {
        try {
            Schema.initAll();} catch (Exception ignored) {}

        showUploadCard();
        startQuizButton.setDisable(true);

        dropZone.setOnDragOver(e -> {
            if (e.getGestureSource() != dropZone && e.getDragboard().hasFiles()) {
                e.acceptTransferModes(javafx.scene.input.TransferMode.COPY);
            }
            e.consume();
        });
        dropZone.setOnDragDropped(e -> {
            var db = e.getDragboard();
            if (db.hasFiles()) handlePickedFile(db.getFiles().get(0));
            e.setDropCompleted(true);
            e.consume();
        });

        uploadButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            var pdf = new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf");
            var docx = new FileChooser.ExtensionFilter("Word Documents (*.docx)", "*.docx");
            var txt = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
            var md = new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md");
            fc.getExtensionFilters().setAll(pdf, docx, txt, md);
            fc.setSelectedExtensionFilter(pdf);
            File f = fc.showOpenDialog(getStage());
            if (f != null) handlePickedFile(f);
        });

        startQuizButton.setOnAction(e -> startQuiz());
        backButton.setOnAction(e -> {
            if (currentIndex > 0) {currentIndex--;renderQuestion();}
        });
        nextButton.setOnAction(e -> onNext());
        takeNewQuizButton.setOnAction(e -> showUploadCard());

        retakeButton.setOnAction(e -> beginRetakeSelected());
        attemptsList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                retakeButton.setDisable(sel == null)
        );
        attemptsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) beginRetakeSelected();
        });
    }

    private void showUploadCard() {uploadCard.setVisible(true);quizCard.setVisible(false);historyCard.setVisible(false);}
    private void showQuizCard() {uploadCard.setVisible(false);quizCard.setVisible(true);historyCard.setVisible(false);}
    private void showHistoryCard() {uploadCard.setVisible(false);quizCard.setVisible(false);historyCard.setVisible(true);}

    private Stage getStage() {
        return (Stage) rootStack.getScene().getWindow();
    }

    private void handlePickedFile(File f) {
        try {
            uploadedContent = FileUtil.readFileContent(f);
            uploadHint.setText("Ready! Click Start Quiz.");
            startQuizButton.setDisable(false);
        } catch (IOException ex) {
            uploadHint.setText("Failed to read file: " + ex.getMessage());
            startQuizButton.setDisable(true);
        }
    }

    private void startQuiz() {
        if (uploadedContent == null) {
            if (outputArea != null) outputArea.setText("Please upload a file first.");
            return;
        }

        if (outputArea != null) outputArea.setText("Generating Multiple Choice quiz...");
        showQuizCard();
        nextButton.setDisable(true);
        backButton.setDisable(true);

        new Thread(() -> {
            try {
                String json = QuizAPI.generateQuiz(uploadedContent, "Multiple Choice");
                List<QuizAPI.McqItem> items = QuizAPI.parseMcqArray(json);
                quizId = QuizMcqRepo.createQuiz("Multiple Choice", 1, items);
                scoreId = QuizDao.startAttempt(quizId, CURRENT_USER_ID);
                mcqQuestions = QuizDao.loadQuestions(quizId);

                Platform.runLater(() -> {
                    selectedByQuestionId.clear();
                    currentIndex = 0;
                    renderQuestion();
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    showUploadCard();
                    if (outputArea != null) outputArea.setText("Failed to load quiz: " + ex.getMessage());
                });
            }
        }).start();
    }

    private void renderQuestion() {
        QuizDao.McqQuestion q = mcqQuestions.get(currentIndex);

        questionCounterLabel.setText("Question " + (currentIndex + 1) + " of " + mcqQuestions.size());
        double pct = (double) currentIndex / mcqQuestions.size();
        progressBar.setProgress(pct);
        percentCompleteLabel.setText((int) Math.round(pct * 100) + "% Complete");

        questionLabel.setText(q.text);

        optionsBox.getChildren().clear();
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < 4; i++) {
            String text = q.options[i];
            RadioButton rb = new RadioButton(text);
            rb.getStyleClass().add("radio-option");
            rb.setUserData(i + 1);            // 1..4
            rb.setToggleGroup(group);
            optionsBox.getChildren().add(rb);
        }

        Integer prev = selectedByQuestionId.get(q.questionID);
        if (prev != null && prev >= 1 && prev <= 4) {
            ((RadioButton) optionsBox.getChildren().get(prev - 1)).setSelected(true);
            nextButton.setDisable(false);
        } else {
            nextButton.setDisable(true);
        }

        group.selectedToggleProperty().addListener((obs, old, sel) -> nextButton.setDisable(sel == null));

        backButton.setDisable(currentIndex == 0);
        nextButton.setText(currentIndex == mcqQuestions.size() - 1 ? "Finish" : "Next");
    }

    private void onNext() {
        QuizDao.McqQuestion q = mcqQuestions.get(currentIndex);
        ToggleGroup group = ((RadioButton) optionsBox.getChildren().get(0)).getToggleGroup();
        Toggle sel = group.getSelectedToggle();
        if (sel == null) return;
        int userOption = (int) sel.getUserData();
        selectedByQuestionId.put(q.questionID, userOption);

        try {
            QuizDao.upsertAnswer(scoreId, q.questionID, userOption, q.correctOption);
        } catch (Exception ex) {
            if (outputArea != null) outputArea.appendText("\n[WARN] Saving answer failed: " + ex.getMessage());
        }

        if (currentIndex < mcqQuestions.size() - 1) {
            currentIndex++;
            renderQuestion();
        } else {
            try {
                int percent = QuizDao.finishAttempt(scoreId, true);
                if (outputArea != null) outputArea.setText("Quiz completed! Score: " + percent + "%");
                loadHistory();
                showHistoryCard();
            } catch (Exception ex) {
                if (outputArea != null) outputArea.setText("Failed to finalize score: " + ex.getMessage());
            }
        }
    }

    private void loadHistory() {
        try {
            var attempts = QuizDao.listAttemptsForUser(CURRENT_USER_ID);
            attemptsList.getItems().setAll(attempts);
            attemptsList.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(QuizDao.AttemptRow a, boolean empty) {
                    super.updateItem(a, empty);
                    if (empty || a == null) {setText(null);return;}
                    setText("Quiz #" + a.quizID + "  —  " + a.score + "%  •  " + a.dateAttempted);
                }
            });
            retakeButton.setDisable(attempts.isEmpty());
        } catch (Exception e) {
            attemptsList.getItems().clear();
            attemptsList.getItems().add(new QuizDao.AttemptRow(0, 0, 0, 0, "[Failed to load]"));
            retakeButton.setDisable(true);
        }
    }
    private void beginRetakeSelected() {
        var sel = attemptsList.getSelectionModel().getSelectedItem();
        if (sel != null) beginRetake(sel.quizID);
    }

    private void beginRetake(long quizIdToRetake) {
        new Thread(() -> {
            try {
                this.quizId = quizIdToRetake;
                this.scoreId = QuizDao.startAttempt(quizIdToRetake, CURRENT_USER_ID);
                this.mcqQuestions = QuizDao.loadQuestions(quizIdToRetake);

                Platform.runLater(() -> {
                    selectedByQuestionId.clear();
                    currentIndex = 0;
                    showQuizCard();
                    renderQuestion();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    showHistoryCard();
                    if (outputArea != null) outputArea.appendText("\n[ERROR] Could not start retake: " + ex.getMessage());
                });
            }
        }).start();
    }
}
