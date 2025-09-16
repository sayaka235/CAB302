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

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Objects;

public class DashboardController {
    private ArrayList<Quiz> quizList = new ArrayList<Quiz>();
    @FXML private ToggleGroup DynamicToggleGroup;
    @FXML private VBox radioButtonsContainer;
    @FXML private ImageView Stars;
    @FXML private Label WelcomeLabel;
    public void initialize() {
        WelcomeLabel.setText("Hello " + UserManager.getInstance().getLoggedInUser().getName());

        try {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/images/stars.png")));
            Stars.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        // make sure ToggleGroup exists
        if (DynamicToggleGroup == null) {
            DynamicToggleGroup = new ToggleGroup();
        }

        String sql = "SELECT quizID, title FROM Quiz WHERE userID = ?";
        quizList.clear();
        System.out.println("Recently attempted quizzes");

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, UserManager.getInstance().getLoggedInUser().getUserID());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String quizTitle = rs.getString("title");
                    Long quizID = rs.getLong("quizID");

                    Quiz quiz = new Quiz(quizID, quizTitle);
                    quizList.add(quiz);
                    QuizManager.getInstance().addQuiz(quiz); // <- add back to manager

                    RadioButton radioButton = new RadioButton(quizTitle);
                    radioButton.setToggleGroup(DynamicToggleGroup);
                    radioButtonsContainer.getChildren().add(radioButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    @FXML private VBox sidebar;

    @FXML
    private void toggleSidebar() {
        if (sidebar.getPrefWidth() > 50) {
            sidebar.setPrefWidth(50); // collapse
        } else {
            sidebar.setPrefWidth(200); // expand
        }
    }

    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }

    @FXML private void userStats(ActionEvent actionEvent) {
        try {
            Navigator.toUserStats();
        } catch (Exception e) {
            e.printStackTrace();alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }
}
