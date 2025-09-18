package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.text.Text;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * Controller for the note summary page.
 * <p>
 * This page shows the full text of the note the user selected
 * in the notes library. It also lets the user go back to the dashboard.
 * This class is connected to {@code noteSummary.fxml}.
 */
public class NoteSummaryInstanceController {
    /** Displays the text of the selected note. */
    @FXML
    private Text NoteSummary;
    /**
     * Sets up the page.
     * <p>
     * Pulls the note chosen from {@link NoteSummaryManager}
     * and shows its content inside the text box.
     */
    @FXML private void initialize(){
        NoteSummary.setText(NoteSummaryManager.getInstance().getCurrentSummaryNote().getNotes());
    }
    /**
     * Goes back to the dashboard when the dashboard button is pressed.
     *
     * @param actionEvent the button click event
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
    /**
     * Shows an alert message if something goes wrong.
     *
     * @param t the type of alert (e.g., ERROR, INFORMATION)
     * @param title the title of the alert window
     * @param msg the text to display in the alert
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}
