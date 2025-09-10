package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.UserManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuizLibraryController {
    private QuizController quizController;
    @FXML private ToggleGroup DynamicToggleGroup;
    @FXML private VBox radioButtonsContainer;
    @FXML private void initialize(){
            String sql = "SELECT quizType, userID, numQuestions, title, imagePath FROM Quiz WHERE userID = ?";
            try (Connection c = Database.getConnection();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, (UserManager.getInstance().getLoggedInUser().getUserID()));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String quizTitle = rs.getString("title");
                        RadioButton radioButton = new RadioButton(quizTitle);
                        radioButtonsContainer.getChildren().add(radioButton);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    @FXML
    private void startQuiz(ActionEvent actionEvent) {
        try{
        RadioButton selectedRadioButton = (RadioButton) DynamicToggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            String selectedOption = selectedRadioButton.getText();
            String sql = "SELECT quizType, userID, numQuestions, title, imagePath FROM Quiz WHERE title = ?";

            switch (selectedOption) {

            }
        } } catch (Exception e){
            alert(Alert.AlertType.ERROR, "No quiz selected", e.getMessage());
        }
        quizController.setIsFromLibrary(true);
        try { Navigator.toQuiz(); }
        catch (Exception e) { e.printStackTrace(); alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage()); }
    }

    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
