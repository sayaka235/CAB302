package org.example.ai_integration.controls;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.RadioButton;
import org.example.ai_integration.*;
import org.example.ai_integration.model.*;

import java.sql.SQLException;
import java.util.*;

import static org.example.ai_integration.model.NotesAPI.parseSummary;
/**
 * Controller for the quiz page of the app.
 * <p>
 * Handles uploading notes, generating quizzes, taking quizzes,
 * and showing results and history of past attempts.
 * Connected to {@code quiz-view.fxml}.
 */
public class QuizController {
    /** The main container of the quiz scene */
    @FXML private StackPane rootStack;
    /** Cards that swap between upload, quiz, history, and loading views */
    @FXML private VBox uploadCard, quizCard, historyCard, loadCard, dropZone;
    /** Hint label for uploading a file */
    @FXML private Label uploadHint;
    /** Spinner to choose how many questions to generate */
    @FXML private Spinner<Integer> questionCountSpinner;
    /** Field where the user types the quiz title */
    @FXML private TextField quizTitleField;
    /** Button to upload an image for the quiz */
    @FXML private Button uploadImageButton;
    /** Label showing the uploaded image file name */
    @FXML private Label imageFileLabel;
    /** Buttons for uploading and starting a quiz */
    @FXML private Button uploadButton, startQuizButton;
    /** Labels and progress bar for showing quiz progress */
    @FXML private Label questionCounterLabel, percentCompleteLabel, questionLabel;
    @FXML private ProgressBar progressBar;
    /** Box containing answer options */
    @FXML private VBox optionsBox;
    /** Navigation buttons for moving through the quiz */
    @FXML private Button backButton, nextButton;
    /** Button to take a brand new quiz */
    @FXML private Button takeNewQuizButton;
    /** List of past attempts */
    @FXML private ListView<QuizDao.AttemptRow> attemptsList;
    /** Button to retake a quiz */
    @FXML private Button retakeButton;
    /** Output area for showing messages */
    @FXML private TextArea outputArea;
    /** Button if quiz is selected from the library */
    @FXML private Button quizSelectedFromLibrary;
    /** ScrollPane for displaying results nicely */
    @FXML private ScrollPane resultsScroll;

    /** The file the user uploaded */
    private File selectedImageFile;
    /** The raw text content from the uploaded file */
    private String uploadedContent = null;
    /** Current quiz id */
    private long quizId;
    /** Current attempt id (score id) */
    private long scoreId;
    /** Tracks which question index the user is up to */
    private int currentIndex = 0;
    /** The summary generated from uploaded notes */
    private String summaryParsed;
    /** The list of MCQ questions in the current quiz */
    private List<QuizDao.McqQuestion> mcqQuestions = new ArrayList<>();
    /** Keeps track of user’s chosen answers */
    private final Map<Long, Integer> selectedByQuestionId = new HashMap<>();

    /** The id of the currently logged-in user */
    private long CURRENT_USER_ID = -1;

    /**
     * Sets up the quiz page when first loaded.
     * <p>
     * Initializes drag & drop upload, image picker, navigation buttons,
     * and checks whether the user came from the quiz library or is starting fresh.
     */
    @FXML
    private void initialize() {
        try {
            CreateSchema.initAll();} catch (Exception ignored) {}

        CURRENT_USER_ID = Long.parseLong(UserManager.getInstance().getLoggedInUser().getUserID());

        try { CreateSchema.initAll(); } catch (Exception ignored) {}
        if(QuizManager.getInstance().getQuiz() == null){
            showUploadCard();
            startQuizButton.setDisable(true);
        }if(!(QuizManager.getInstance().getQuiz() == null)){
            quizId = QuizManager.getInstance().getQuiz().getQuizID();
            showQuizCard();
            startQuizFromLibrary();
        }


        // Drag & drop upload
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

        // Image picker
        uploadImageButton.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File f = fc.showOpenDialog(getStage());
            if (f != null) {
                selectedImageFile = f;
                imageFileLabel.setText(f.getName());
            }
        });

        startQuizButton.setOnAction(e -> startQuiz());
        backButton.setOnAction(e -> { if (currentIndex > 0) { currentIndex--; renderQuestion(); } });
        nextButton.setOnAction(e -> onNext());
        takeNewQuizButton.setOnAction(e -> goToQuizLibrary());

        retakeButton.setOnAction(e -> beginRetakeSelected());
        attemptsList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                retakeButton.setDisable(sel == null));
        attemptsList.setOnMouseClicked(e -> { if (e.getClickCount() == 2) beginRetakeSelected(); });
    }
    /** Shows the upload card (where user uploads notes to start a new quiz). */
    private void showUploadCard() {
        uploadCard.setVisible(true);  uploadCard.setManaged(true);
        quizCard.setVisible(false);   quizCard.setManaged(false);
        loadCard.setVisible(false); loadCard.setManaged(false);
        historyCard.setVisible(false); historyCard.setManaged(false);

        uploadedContent = null;
        startQuizButton.setDisable(true);
        uploadHint.setText("Drop a file here or choose one to start a new quiz.");
        selectedByQuestionId.clear();
        mcqQuestions.clear();
        currentIndex = 0;

        restoreQuizNavHandlers();
    }
    /** Shows the quiz-taking card (where the questions are). */
    private void showQuizCard()   {
        uploadCard.setVisible(false); uploadCard.setManaged(false);
        loadCard.setVisible(false); loadCard.setManaged(false);
        quizCard.setVisible(true);    quizCard.setManaged(true);
        historyCard.setVisible(false); historyCard.setManaged(false);
    }
    /** Shows the history card (list of past attempts). */
    private void showHistoryCard(){
        uploadCard.setVisible(false); uploadCard.setManaged(false);
        loadCard.setVisible(false); loadCard.setManaged(false);
        quizCard.setVisible(false);   quizCard.setManaged(false);
        historyCard.setVisible(true); historyCard.setManaged(true);
    }
    /** Shows the loading card (used when quiz is being generated). */
    private void showLoadCard(){
        uploadCard.setVisible(false); uploadCard.setManaged(false);
        quizCard.setVisible(false);   quizCard.setManaged(false);
        historyCard.setVisible(false); historyCard.setManaged(false);
        loadCard.setVisible(true); loadCard.setManaged(true);
    }

    /** Gets the stage (window) the quiz is running in. */
    private Stage getStage() {
        return (Stage) rootStack.getScene().getWindow();
    }
    /**
     * Navigates back to the dashboard page.
     * @param actionEvent button click
     */
    @FXML
    private void goToDashBoard(ActionEvent actionEvent) {
        try {
            Navigator.toDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }

    /** Opens the quiz library page. */
    private void goToQuizLibrary(){
            try {
                Navigator.toQuizLibrary();
            } catch (Exception e) {
                e.printStackTrace(); alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
            }
    }
    /**
     * Reads the content of the uploaded file and prepares it for quiz generation.
     * @param f the file uploaded by the user
     */
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
    /** Starts a quiz that was chosen from the quiz library. */
    private void startQuizFromLibrary(){
        new Thread(() -> {
            try {
                scoreId = QuizDao.startAttempt(quizId, CURRENT_USER_ID);
                mcqQuestions = QuizDao.loadQuestions(quizId);

                Platform.runLater(() -> {
                    selectedByQuestionId.clear();
                    currentIndex = 0;
                    restoreQuizNavHandlers();
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
    /** Starts generating and loading a brand new quiz from uploaded notes. */
    private void startQuiz() {
        if (uploadedContent == null) {
            if (outputArea != null) outputArea.setText("Please upload a file first.");
            return;
        }

        if (outputArea != null) outputArea.setText("Generating Multiple Choice quiz...");
        showQuizCard();
        restoreQuizNavHandlers();
        nextButton.setDisable(true);
        backButton.setDisable(true);

        new Thread(() -> {
            try {
                showLoadCard();
                int numQuestions = questionCountSpinner != null ? questionCountSpinner.getValue() : 5;
                String title = quizTitleField.getText();
                String imagePath = selectedImageFile != null ? selectedImageFile.getAbsolutePath() : null;
                String jsonNotes = NotesAPI.generateSummary(uploadedContent);
                summaryParsed = parseSummary(jsonNotes);
                String json = QuizAPI.generateQuiz(uploadedContent, "Multiple Choice", numQuestions);
                List<QuizAPI.McqItem> items = QuizAPI.parseMcqArray(json);


                long notesID = NoteSummaryRepo.createNotes(summaryParsed, title, CURRENT_USER_ID);
                quizId = QuizMcqRepo.createQuiz("Multiple Choice", CURRENT_USER_ID, items, title, imagePath);
                scoreId = QuizDao.startAttempt(quizId, CURRENT_USER_ID);
                mcqQuestions = QuizDao.loadQuestions(quizId);

                Platform.runLater(() -> {
                    showQuizCard();
                    selectedByQuestionId.clear();
                    currentIndex = 0;
                    restoreQuizNavHandlers();
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
    /** Renders the current question and answer options on screen. */
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
            rb.setUserData(i + 1);
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
    /** Handles what happens when the user clicks “Next” (or Finish). */
    private void onNext() {
        QuizDao.McqQuestion q = mcqQuestions.get(currentIndex);

        RadioButton selectedBtn = null;
        for (var node : optionsBox.getChildren()) {
            if (node instanceof RadioButton rb && rb.isSelected()) {
                selectedBtn = rb;
                break;
            }
        }
        if (selectedBtn == null) {
            System.out.println("[onNext] No option selected.");
            if (outputArea != null) outputArea.appendText("\nPlease select an option.");
            return;
        }

        int userOption = (int) selectedBtn.getUserData();
        selectedByQuestionId.put(q.questionID, userOption);

        try {
            System.out.println("[onNext] savePickedAnswer(scoreId=" + scoreId + ", qId=" + q.questionID + ", opt=" + userOption + ")");
            QuizDao.savePickedAnswer(scoreId, q.questionID, userOption);
        } catch (Exception ex) {
            System.out.println("[onNext] ERROR saving answer");
            ex.printStackTrace();
            if (outputArea != null) outputArea.appendText("\n[WARN] Saving answer failed: " + ex.getMessage());
        }

        boolean isLast = (currentIndex >= mcqQuestions.size() - 1);
        if (!isLast) {
            currentIndex++;
            renderQuestion();
            return;
        }
        try {
            int percent = QuizDao.finishAttempt(scoreId, true);
            var rows = QuizDao.getAttemptResults(scoreId);

            showResults(rows, percent);

        } catch (Exception ex) {
            if (outputArea != null) outputArea.setText("Failed to finalize or load results: " + ex.getMessage());
            loadHistory();
            showHistoryCard();
        }
    }
    /**
     * Shows the results screen after finishing the quiz.
     * @param rows list of result rows (with question text, options, correctness)
     * @param percent the overall score in percent
     */
    private void showResults(List<QuizDao.ResultRow> rows, int percent) {
        showQuizCard();

        questionCounterLabel.setText("Quiz Complete!");
        percentCompleteLabel.setText(percent + "%");
        progressBar.setProgress(1.0);
        questionLabel.setText("Here's your performance summary and question breakdown");

        if (resultsScroll != null) {
            resultsScroll.setFitToWidth(true);
            resultsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            resultsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            resultsScroll.setFocusTraversable(false);
        }

        optionsBox.getChildren().clear();
        optionsBox.setFillWidth(true);

        long correct = rows.stream().filter(r -> r.isCorrect).count();
        long incorrect = rows.size() - correct;

        Label correctLbl = new Label("Correct Answers\n" + correct);
        correctLbl.getStyleClass().addAll("summary-badge", "summary-correct");
        Label incorrectLbl = new Label("Incorrect Answers\n" + incorrect);
        incorrectLbl.getStyleClass().addAll("summary-badge", "summary-incorrect");

        var summary = new javafx.scene.layout.HBox(16, correctLbl, incorrectLbl);
        summary.getStyleClass().add("summary-row");
        optionsBox.getChildren().add(summary);

        for (int i = 0; i < rows.size(); i++) {
            var r = rows.get(i);
            int chosenIdx  = Math.max(1, Math.min(4, r.chosen)) - 1;
            int correctIdx = Math.max(1, Math.min(4, r.correct)) - 1;

            Label qTitle = new Label("Question " + (i + 1));
            qTitle.getStyleClass().add("q-title");

            Label qText = new Label(r.text);
            qText.getStyleClass().add("q-text");
            qText.setWrapText(true);
            qText.setMaxWidth(Double.MAX_VALUE);

            var opts = new javafx.scene.layout.VBox(8);
            for (int k = 0; k < 4; k++) {
                Label opt = new Label(r.options[k]);
                opt.setWrapText(true);
                opt.setMaxWidth(Double.MAX_VALUE);
                opt.getStyleClass().add("opt");

                if (k == correctIdx)                   opt.getStyleClass().add("opt-correct");
                if (k == chosenIdx)                    opt.getStyleClass().add("opt-chosen");
                if (k == chosenIdx && !r.isCorrect)    opt.getStyleClass().add("opt-wrong");

                opts.getChildren().add(opt);
            }

            var card = new javafx.scene.layout.VBox(8, qTitle, qText, opts);
            card.getStyleClass().addAll("q-card", r.isCorrect ? "q-card-correct" : "q-card-incorrect");
            card.setMaxWidth(Double.MAX_VALUE);

            optionsBox.getChildren().add(card);
        }

        backButton.setDisable(false);
        backButton.setText("History");
        backButton.setOnAction(e -> { loadHistory(); showHistoryCard(); });

        nextButton.setDisable(false);
        nextButton.setText("Take New Quiz");
        nextButton.setOnAction(e -> goToQuizLibrary());  // this will reset state (see section B)
    }
    /** Resets the navigation buttons for quiz-taking mode. */
    private void restoreQuizNavHandlers() {
        backButton.setText("Back");
        backButton.setDisable(true);
        backButton.setOnAction(e -> { if (currentIndex > 0) { currentIndex--; renderQuestion(); }});

        nextButton.setText("Next");
        nextButton.setDisable(true);
        nextButton.setOnAction(e -> onNext());
    }

    /** Loads the quiz attempt history for the current user. */
    private void loadHistory() {
        try {
            var attempts = QuizDao.listAttemptsForUser(CURRENT_USER_ID);
            attemptsList.getItems().setAll(attempts);

            attemptsList.setCellFactory(lv -> new ListCell<>() {
                private final ImageView imageView = new ImageView();

                @Override protected void updateItem(QuizDao.AttemptRow a, boolean empty) {
                    super.updateItem(a, empty);
                    if (empty || a == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }

                    String title = (a.title != null && !a.title.isBlank())
                            ? a.title
                            : "Quiz #" + a.quizID;

                    setText(title + " — " + a.score + "% • " + a.dateAttempted);

                    if (a.imagePath != null && !a.imagePath.isBlank()) {
                        imageView.setImage(new Image(new File(a.imagePath).toURI().toString(), 40, 40, true, true));
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            });

            retakeButton.setDisable(attempts.isEmpty());
        } catch (Exception e) {
            attemptsList.getItems().clear();
            attemptsList.getItems().add(new QuizDao.AttemptRow(0, 0, 0, 0,
                    "[Failed to load]", "Error", ""));
            retakeButton.setDisable(true);
        }
    }
    /** Starts retaking whichever attempt the user selected in the history list. */
    private void beginRetakeSelected() {
        var sel = attemptsList.getSelectionModel().getSelectedItem();
        if (sel != null) beginRetake(sel.quizID);
    }
    /**
     * Shows a popup alert with the given message.
     * @param t the alert type (ERROR, INFO, etc.)
     * @param title the title of the alert window
     * @param msg the message to show
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
    /**
     * Starts retaking a specific quiz attempt by its ID.
     * @param quizIdToRetake the quiz ID to retake
     */
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
                    restoreQuizNavHandlers();
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
