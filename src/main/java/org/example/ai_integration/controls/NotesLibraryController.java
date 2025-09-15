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

public class NotesLibraryController {
    private ArrayList<NoteSummary> notesList = new ArrayList<>();

    @FXML private ToggleGroup DynamicToggleGroup;
    @FXML private VBox radioButtonsContainer;

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
                    /*String extNotesURL = rs.getString("extNotesURL");*/

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

    private static void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    private void goToDashBoard(ActionEvent actionEvent) {
        try {
            Navigator.toDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, "Navigation error", e.getMessage());
        }
    }

    @FXML private void readNotes(ActionEvent actionEvent) {
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
            alert(Alert.AlertType.ERROR, "No quiz selected", e.getMessage());
        }
    }
}
