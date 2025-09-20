package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.UserManager;


public class SidebarController {
    @FXML
    private javafx.scene.layout.VBox sidebar;
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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Logout");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to log out?");

        // Wait for user response
        confirm.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    UserManager.getInstance().setLoggedInUser(null);
                    Navigator.toSignup();
                } catch (Exception e) {
                    e.printStackTrace();
                    alert(Alert.AlertType.ERROR, "Logout error", e.getMessage());
                }
            }
        });
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
