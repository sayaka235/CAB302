package org.example.ai_integration.model;

import javafx.scene.control.Alert;
import org.example.ai_integration.Navigator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Model class for managing quizzes stored in the database.
 * <p>
 * Provides methods to fetch quizzes for the currently logged-in user.
 */
public class QuizLibrary {
    /**
     * Fetches all quizzes belonging to the currently logged-in user.
     * <p>
     * Connects to the database, runs a query on the {@code Quiz} table,
     * and retrieves quiz information such as type, number of questions,
     * title, and image path. Currently, the method does not process
     * or store the retrieved data.
     */
    private void fetchQuizzesForUser(){
    String sql = "SELECT quizType, userID, numQuestions, title, imagePath FROM Quiz WHERE userID = ?";
    try (Connection c = Database.getConnection();
    PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, (UserManager.getInstance().getLoggedInUser().getUserID()));
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {

            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
