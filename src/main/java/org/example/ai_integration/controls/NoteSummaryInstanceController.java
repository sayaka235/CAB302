package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import org.example.ai_integration.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class NoteSummaryInstanceController {
    @FXML
    private Label notes;
    @FXML private void initialize(){
        notes.setText(NoteSummaryManager.getInstance().getCurrentSummaryNote().getNotes());
    }
}
