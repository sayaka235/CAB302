package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.UserManager;

public class DashboardController {
    @FXML private Label WelcomeLabel;
    public void initialize(){
        WelcomeLabel.setText("Hello " + UserManager.getInstance().getLoggedInUser().getName());
    }

    @FXML
    private void fileUpload(ActionEvent actionEvent) {
        try {
            Navigator.toQuiz();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void quizLibrary(ActionEvent actionEvent) {
        try {
            Navigator.toQuizLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
