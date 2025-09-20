package org.example.ai_integration.controls;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.ai_integration.model.UserManager;
import org.example.ai_integration.model.User;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the user stats page of the application.
 * <p>
 * Displays a personalized greeting and sets up the page
 * so the user can view their performance stats.
 * Connected to {@code UserStatsScene.fxml}.
 */
public class UserStatsController implements Initializable {

    /** Label at the top of the page that greets the user */
    @FXML
    private Label headingLabel;

    /**
     * Initializes the stats page.
     * <p>
     * Checks if a user is logged in and updates the heading with
     * their first name. If no user is logged in, shows a generic greeting.
     * @param url the location used to resolve relative paths (not used here)
     * @param rb the resource bundle for localization (not used here)
     */
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
