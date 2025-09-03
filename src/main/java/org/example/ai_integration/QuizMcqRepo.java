package org.example.ai_integration;

import org.example.ai_integration.model.Database;
import org.example.ai_integration.model.QuizAPI;

import java.sql.*;
import java.util.List;

public final class QuizMcqRepo {
    private QuizMcqRepo() {}

    public static long createQuiz(String quizType, int difficulty, List<QuizAPI.McqItem> qs) throws SQLException {
        try (Connection c = Database.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO Quiz(difficulty, score, numQuestions, numAttempts, numSuccesses) VALUES(?, 0, ?, 0, 0)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, difficulty);
                ps.setInt(2, qs.size());
                ps.executeUpdate();
                long quizId;
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No quizID generated");
                    quizId = rs.getLong(1);
                }
                try (PreparedStatement qps = c.prepareStatement("""
                    INSERT INTO QuizQuestions(quizID, questionText, option1, option2, option3, option4, correctOptionNumber)
                    VALUES(?,?,?,?,?,?,?)
                """)) {
                    for (QuizAPI.McqItem q : qs) {
                        if (q.options == null || q.options.size() != 4)
                            throw new SQLException("MCQ item must have exactly 4 options.");
                        qps.setLong(1, quizId);
                        qps.setString(2, q.question);
                        qps.setString(3, q.options.get(0));
                        qps.setString(4, q.options.get(1));
                        qps.setString(5, q.options.get(2));
                        qps.setString(6, q.options.get(3));
                        qps.setInt(7, q.correct_index); // 1..4
                        qps.addBatch();
                    }
                    qps.executeBatch();
                }
                c.commit();
                return quizId;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }
}
