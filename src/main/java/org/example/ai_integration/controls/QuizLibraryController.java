package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
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

/**
 * Controller for the quiz library page.
 * <p>
 * Displays all of the user’s previously created or attempted quizzes
 * and allows them to select one to retake or continue.
 * Connected to {@code quizLibrary.fxml}.
 */
public class QuizLibraryController {
    /** Holds the list of quizzes loaded from the database */
    private ArrayList<Quiz> quizList = new ArrayList<>();

    /** Toggle group so only one quiz radio button can be selected at a time */
    @FXML private ToggleGroup DynamicToggleGroup;
    /** The container that displays quiz options as radio buttons */
    @FXML private VBox radioButtonsContainer;

    /**
     * Initializes the quiz library.
     * <p>
     * Loads all quizzes for the currently logged-in user from the database
     * and creates a radio button for each quiz inside the container.
     */
    @FXML
    private void initialize(){
        String sql = "SELECT quizID, title FROM Quiz WHERE userID = ?";
        QuizManager.getInstance().clearQuiz();
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, (UserManager.getInstance().getLoggedInUser().getUserID()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String quizTitle = rs.getString("title");
                    Long quizID = rs.getLong("quizID");
                    Quiz quiz = new Quiz(quizID, quizTitle);
                    quizList.add(quiz);
                    RadioButton radioButton = new RadioButton(quizTitle);
                    radioButtonsContainer.getChildren().add(radioButton);
                    radioButton.setToggleGroup(DynamicToggleGroup);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the quiz that the user selected from the library.
     * <p>
     * Finds the chosen quiz, sets it in the {@code QuizManager},
     * and navigates to the quiz page.
     * @param actionEvent the event triggered when the start button is clicked
     */
    @FXML
    private void startQuiz(ActionEvent actionEvent) {
        try {
            RadioButton selectedRadioButton = (RadioButton) DynamicToggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedOption = selectedRadioButton.getText();
                Quiz selectedQuiz = null;
                for (Quiz quiz : quizList) {
                    if (quiz.geTitle().equals(selectedOption)) {
                        selectedQuiz = quiz;
                        break;
                    }
                }
                QuizManager.getInstance().setQuiz(selectedQuiz);
                Navigator.toQuiz();
            }
        }
        catch(Exception e){
            alert(Alert.AlertType.ERROR, "No quiz selected", e.getMessage());
        }
    }

    /**
     * Shows an alert popup with a given message.
     * @param t the alert type (e.g., ERROR, WARNING, INFORMATION)
     * @param title the title of the alert box
     * @param msg the content of the message
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Navigates back to the dashboard screen.
     * @param actionEvent the event triggered when the dashboard button is clicked
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
}
