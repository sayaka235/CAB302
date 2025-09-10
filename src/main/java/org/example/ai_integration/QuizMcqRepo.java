package org.example.ai_integration;

import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.QuizAPI;

import java.sql.*;
import java.util.List;

public final class QuizMcqRepo {
    private QuizMcqRepo() {}
    public static long createQuiz(String quizType, long userId,
                                  List<QuizAPI.McqItem> items,
                                  String title,
                                  String imagePath) throws SQLException {
        try (Connection c = Database.getConnection()) {
            // Insert quiz metadata (with title & imagePath)
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Quiz (quizType, userID, numQuestions, title, imagePath) VALUES (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, quizType);
                ps.setLong(2, userId);
                ps.setInt(3, items.size());
                ps.setString(4, title);
                ps.setString(5, imagePath);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("Failed to create quiz");
                    long quizId = rs.getLong(1);

                    // Insert questions
                    try (PreparedStatement qs = c.prepareStatement(
                            "INSERT INTO QuizQuestions (quizID, questionText, option1, option2, option3, option4, correctOptionNumber) " +
                                    "VALUES (?,?,?,?,?,?,?)")) {
                        for (QuizAPI.McqItem item : items) {
                            qs.setLong(1, quizId);
                            qs.setString(2, item.question);
                            qs.setString(3, item.options.get(0));
                            qs.setString(4, item.options.get(1));
                            qs.setString(5, item.options.get(2));
                            qs.setString(6, item.options.get(3));
                            qs.setInt(7, item.correct_index);
                            qs.addBatch();
                        }
                        qs.executeBatch();
                    }
                    return quizId;
                }
            }
        }
    }
}
