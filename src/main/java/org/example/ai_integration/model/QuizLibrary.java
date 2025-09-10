package org.example.ai_integration.model;

import javafx.scene.control.Alert;
import org.example.ai_integration.Navigator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class QuizLibrary {
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
