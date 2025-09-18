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

import java.util.*;

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
        // Removed CreateSchema.initAll() call
        CURRENT_USER_ID = Long.parseLong(UserManager.getInstance().getLoggedInUser().getUserID());

        if (QuizManager.getInstance().getQuiz() == null) {
            showUploadCard();
            startQuizButton.setDisable(true);
        } else {
            quizId = QuizManager.getInstance().getQuiz().getQuizID();
            showQuizCard();
            startQuizFromLibrary();
        }

        // Drag & drop upload setup
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

        // File upload button
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
    private void showUploadCard() {   }

    /** Shows the quiz-taking card (where the questions are). */
    private void showQuizCard() {   }

    /** Shows the history card (list of past attempts). */
    private void showHistoryCard() {   }

    /** Shows the loading card (used when quiz is being generated). */
    private void showLoadCard() {   }

    /** Gets the stage (window) the quiz is running in. */
    private Stage getStage() { return (Stage) rootStack.getScene().getWindow(); }

    /**
     * Navigates back to the dashboard page.
     * @param actionEvent button click
     */
    @FXML
    private void goToDashBoard(ActionEvent actionEvent) {   }

    /** Opens the quiz library page. */
    private void goToQuizLibrary() {   }

    /**
     * Reads the content of the uploaded file and prepares it for quiz generation.
     * @param f the file uploaded by the user
     */
    private void handlePickedFile(File f) {   }

    /** Starts a quiz that was chosen from the quiz library. */
    private void startQuizFromLibrary() {   }

    /** Starts generating and loading a brand new quiz from uploaded notes. */
    private void startQuiz() {   }

    /** Renders the current question and answer options on screen. */
    private void renderQuestion() {   }

    /** Handles what happens when the user clicks “Next” (or Finish). */
    private void onNext() {   }

    /**
     * Shows the results screen after finishing the quiz.
     * @param rows list of result rows (with question text, options, correctness)
     * @param percent the overall score in percent
     */
    private void showResults(List<QuizDao.ResultRow> rows, int percent) {  }

    /** Resets the navigation buttons for quiz-taking mode. */
    private void restoreQuizNavHandlers() {  }

    /** Loads the quiz attempt history for the current user. */
    private void loadHistory() {  }

    /** Starts retaking whichever attempt the user selected in the history list. */
    private void beginRetakeSelected() {}

    /**
     * Shows a popup alert with the given message.
     * @param t the alert type (ERROR, INFO, etc.)
     * @param title the title of the alert window
     * @param msg the message to show
     */
    private static void alert(Alert.AlertType t, String title, String msg) { }

    /**
     * Starts retaking a specific quiz attempt by its ID.
     * @param quizIdToRetake the quiz ID to retake
     */
    private void beginRetake(long quizIdToRetake) { }
}
