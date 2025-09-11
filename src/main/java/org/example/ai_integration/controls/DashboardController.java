package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.UserManager;

import javax.swing.*;

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
            e.printStackTrace();alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }
    @FXML
    private void notesLibrary(ActionEvent actionEvent){
        try{
            Navigator.toNotesLibrary();
        } catch (Exception e){
            e.printStackTrace();alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }
    @FXML
    private void quizLibrary(ActionEvent actionEvent) {
        try {
            Navigator.toQuizLibrary();
        } catch (Exception e) {
            e.printStackTrace();alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }
    @FXML
    private void logOut(ActionEvent actionEvent) {
        try {
            Navigator.toSignup();
            UserManager.getInstance().setLoggedInUser(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
