package org.example.ai_integration.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.example.ai_integration.Navigator;
import org.example.ai_integration.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Controller for the notes library page.
 * <p>
 * This shows a list of saved notes for the logged in user
 * and lets them pick one to read or go back to the dashboard.
 * This class is connected to {@code notesLibrary.fxml}.
 */
public class NotesLibraryController {
    /** A list that stores all the notes pulled from the database. */
    private ArrayList<NoteSummary> notesList = new ArrayList<>();

    /** Groups all the note radio buttons so only one can be selected at a time. */
    @FXML private ToggleGroup DynamicToggleGroup;

    /** The container that holds the note radio buttons. */
    @FXML private VBox radioButtonsContainer;

    /**
     * Sets up the notes library page.
     * <p>
     * Connects to the database, finds all the notes for the logged-in user,
     * and creates radio buttons so the user can pick one.
     */
    @FXML
    private void initialize() {
        String sql = "SELECT noteID, title, notes FROM noteSummary WHERE userID = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, UserManager.getInstance().getLoggedInUser().getUserID());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long noteID = rs.getLong("noteID");
                    String title = rs.getString("title");
                    String notesContent = rs.getString("notes");

                    NoteSummary note = new NoteSummary(noteID, title, notesContent);
                    notesList.add(note);

                    RadioButton radioButton = new RadioButton(title);
                    radioButton.setToggleGroup(DynamicToggleGroup);
                    radioButtonsContainer.getChildren().add(radioButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows an alert message if something goes wrong.
     *
     * @param t the type of alert (e.g., ERROR, WARNING, INFO)
     * @param title the title of the alert window
     * @param msg the text shown in the alert
     */
    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
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
     * Opens the notes that the user selected.
     * <p>
     * Finds the chosen note from the list and sends it to
     * {@link NoteSummaryManager} so it can be shown on the next page.
     *
     * @param actionEvent the button click event
     */
    @FXML
    private void readNotes(ActionEvent actionEvent) {
        try {
            RadioButton selectedRadioButton = (RadioButton) DynamicToggleGroup.getSelectedToggle();
            if (selectedRadioButton != null) {
                String selectedOption = selectedRadioButton.getText();
                NoteSummary selectedNotes = null;
                for (NoteSummary note : notesList) {
                    if (note.getTitle().equals(selectedOption)) {
                        selectedNotes = note;
                        break;
                    }
                }
                NoteSummaryManager.getInstance().setNote(selectedNotes);
                Navigator.toNoteSummary();
            }
        }
        catch(Exception e){
            alert(Alert.AlertType.ERROR, "No note selected", e.getMessage());
        }
    }
}
