package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.ai_integration.model.UserManager;
import org.example.ai_integration.model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class UserStatsController implements Initializable {

    @FXML
    private Label headingLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserManager userManager = UserManager.getInstance();

        if (userManager.isLoggedIn()) {
            User loggedInUser = userManager.getLoggedInUser();
            String firstName = loggedInUser.getFirstname();

            headingLabel.setText("Hello " + firstName + ", see your performance below.");
        } else {
            // Handle the case where no user is logged in
            headingLabel.setText("Hello, see your performance below.");
        }
    }
}