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

public class QuizLibraryController {
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();


    @FXML private ToggleGroup DynamicToggleGroup;
    @FXML private VBox radioButtonsContainer;
    @FXML private void initialize(){
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

    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
