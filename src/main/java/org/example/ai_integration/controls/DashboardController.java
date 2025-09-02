package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.ai_integration.model.User;
import org.example.ai_integration.model.UserManager;

public class DashboardController {
    @FXML private Label WelcomeLabel;
    public void initialize(){
        WelcomeLabel.setText("Hello " + UserManager.getInstance().getLoggedInUser().getName());
    }

}
