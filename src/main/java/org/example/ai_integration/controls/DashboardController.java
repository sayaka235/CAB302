package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.Quiz;
import org.example.ai_integration.model.QuizManager;
import org.example.ai_integration.model.UserManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for the application's dashboard (home page).
 * <p>
 * Provides navigation to other pages (quiz, notes library, stats, etc.)
 * and displays personalized information for the logged-in user.
 * This class is connected to {@code DashboardScene.fxml}.
 */
public class DashboardController {
    /** A list of quizzes retrieved from the database. */
    private ArrayList<Quiz> quizList = new ArrayList<>();

    /** Toggle group used to group dynamically created quiz radio buttons. */
    @FXML private ToggleGroup DynamicToggleGroup;

    /** The container for dynamically generated quiz radio buttons. */
    @FXML private VBox radioButtonsContainer;

    /** Displays decorative star icons on the dashboard. */
    @FXML private ImageView Stars;

    /** Displays the welcome message with the user's name. */
    @FXML private Label WelcomeLabel;

    /** The sidebar container that can be toggled open or closed. */
    @FXML private VBox sidebar;

    /**
     * Initialises the dashboard view.
     * <p>
     * - Displays the current user's name.<br>
     * - Loads a decorative image.<br>
     * - Fetches recent quizzes from the database.<br>
     * - Dynamically generates radio buttons for each quiz.
     */
    public void initialize() {
        WelcomeLabel.setText("Hello " + UserManager.getInstance().getLoggedInUser().getName());

        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/stars.png")));
            Stars.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        if (DynamicToggleGroup == null) {
            DynamicToggleGroup = new ToggleGroup();
        }

        String sql = "SELECT quizID, title FROM Quiz WHERE userID = ?";
        quizList.clear();

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, UserManager.getInstance().getLoggedInUser().getUserID());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String quizTitle = rs.getString("title");
                    Long quizID = rs.getLong("quizID");

                    Quiz quiz = new Quiz(quizID, quizTitle);
                    quizList.add(quiz);
                    QuizManager.getInstance().addQuiz(quiz);

                    RadioButton radioButton = new RadioButton(quizTitle);
                    radioButton.setToggleGroup(DynamicToggleGroup);
                    radioButtonsContainer.getChildren().add(radioButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the quiz upload scene.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void fileUpload(ActionEvent actionEvent) {
        try {
            Navigator.toQuiz();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }

    /**
     * Navigates to the notes library scene.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void notesLibrary(ActionEvent actionEvent) {
        try {
            Navigator.toNotesLibrary();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }

    /**
     * Navigates to the quiz library scene.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void quizLibrary(ActionEvent actionEvent) {
        try {
            Navigator.toQuizLibrary();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }

    /**
     * Logs out the current user and navigates back to the signup scene.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void logOut(ActionEvent actionEvent) {
        try {
            Navigator.toSignup();
            UserManager.getInstance().setLoggedInUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles the sidebar width between collapsed and expanded states.
     */
    @FXML
    private void toggleSidebar() {
        if (sidebar.getPrefWidth() > 50) {
            sidebar.setPrefWidth(50); // collapse
        } else {
            sidebar.setPrefWidth(200); // expand
        }
    }

    /**
     * Displays an alert dialog with the given parameters.
     *
     * @param t     the type of alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert window
     * @param msg   the content message of the alert
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Navigates to the user statistics scene.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void userStats(ActionEvent actionEvent) {
        try {
            Navigator.toUserStats();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }
}
