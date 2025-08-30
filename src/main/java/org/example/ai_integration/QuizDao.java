package org.example.ai_integration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class QuizDao {
    private QuizDao() {}

    public static final class McqQuestion {
        public final long questionID;
        public final String text;
        public final String[] options;
        public final int correctOption;
        public McqQuestion(long id, String text, String o1, String o2, String o3, String o4, int correct) {
            this.questionID = id;
            this.text = text;
            this.options = new String[]{o1, o2, o3, o4};
            this.correctOption = correct;
        }
    }

    public static long startAttempt(long quizId, long userId) throws SQLException {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO QuizScoreArray(quizID, userID, score, improvementRate) VALUES(?,?,0,NULL)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, quizId);
            ps.setLong(2, userId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            System.out.println("DEBUG startAttempt quizId=" + quizId + ", userId=" + userId);

        }
        throw new SQLException("Failed to create attempt.");

    }

    public static List<McqQuestion> loadQuestions(long quizId) throws SQLException {
        String sql = """
            SELECT questionID, questionText, option1, option2, option3, option4, correctOptionNumber
            FROM QuizQuestions
            WHERE quizID = ?
            ORDER BY questionID
        """;
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                List<McqQuestion> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(new McqQuestion(
                            rs.getLong("questionID"),
                            rs.getString("questionText"),
                            rs.getString("option1"),
                            rs.getString("option2"),
                            rs.getString("option3"),
                            rs.getString("option4"),
                            rs.getInt("correctOptionNumber")
                    ));
                }
                return list;
            }
        }
    }

    public static void recordAnswer(long scoreId, long questionId, int userOptionNumber, int correctOptionNumber)
            throws SQLException {
        int isCorrect = (userOptionNumber == correctOptionNumber) ? 1 : 0;
        String sql = "INSERT INTO QuizAttemptAnswer(scoreID, questionID, userOptionNumber, isCorrect) VALUES(?,?,?,?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, scoreId);
            ps.setLong(2, questionId);
            ps.setInt(3, userOptionNumber);
            ps.setInt(4, isCorrect);
            ps.executeUpdate();
        }
    }

    public static int finishAttempt(long scoreId, boolean updateQuizRollup) throws SQLException {
        int total = 0, correct = 0;
        long quizId;

        try (Connection c = Database.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT COUNT(*) AS total, COALESCE(SUM(isCorrect),0) AS correct
                FROM QuizAttemptAnswer WHERE scoreID = ?
            """)) {
                ps.setLong(1, scoreId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getInt("total");
                        correct = rs.getInt("correct");
                    }
                }
            }

            int percent = (total == 0) ? 0 : Math.round(100f * correct / total);

            try (PreparedStatement upd = c.prepareStatement(
                    "UPDATE QuizScoreArray SET score = ?, dateAttempted = datetime('now') WHERE scoreID = ?")) {
                upd.setInt(1, percent);
                upd.setLong(2, scoreId);
                upd.executeUpdate();
            }

            if (updateQuizRollup) {
                try (PreparedStatement ps = c.prepareStatement("SELECT quizID FROM QuizScoreArray WHERE scoreID = ?")) {
                    ps.setLong(1, scoreId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Attempt not found: " + scoreId);
                        quizId = rs.getLong("quizID");
                    }
                }
                int attempts = 0, successes = 0;
                try (PreparedStatement ps = c.prepareStatement("""
                    SELECT COUNT(*) AS attempts,
                           SUM(CASE WHEN score >= 50 THEN 1 ELSE 0 END) AS successes
                    FROM QuizScoreArray WHERE quizID = ?
                """)) {
                    ps.setLong(1, quizId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            attempts = rs.getInt("attempts");
                            successes = rs.getInt("successes");
                        }
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE Quiz SET numAttempts = ?, numSuccesses = ? WHERE quizID = ?")) {
                    ps.setInt(1, attempts);
                    ps.setInt(2, successes);
                    ps.setLong(3, quizId);
                    ps.executeUpdate();
                }
            }
            return percent;
        }
    }
    public static final class AttemptRow {
        public final long scoreID, quizID; public final int score, numQuestions;
        public final String dateAttempted;
        public AttemptRow(long s,long q,int sc,int n,String d){scoreID=s;quizID=q;score=sc;numQuestions=n;dateAttempted=d;}
    }

    public static java.util.List<AttemptRow> listAttemptsForUser(long userId) throws SQLException {
        String sql = """
        SELECT qsa.scoreID, qsa.quizID, qsa.score, q.numQuestions, qsa.dateAttempted
        FROM QuizScoreArray qsa
        JOIN Quiz q ON q.quizID = qsa.quizID
        WHERE qsa.userID = ?
        ORDER BY qsa.dateAttempted DESC, qsa.scoreID DESC
    """;
        try (var c = Database.getConnection(); var ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (var rs = ps.executeQuery()) {
                var out = new java.util.ArrayList<AttemptRow>();
                while (rs.next())
                    out.add(new AttemptRow(
                            rs.getLong("scoreID"),
                            rs.getLong("quizID"),
                            rs.getInt("score"),
                            rs.getInt("numQuestions"),
                            rs.getString("dateAttempted")));
                return out;
            }
        }
    }
    public static void upsertAnswer(long scoreId, long questionId, int userOptionNumber, int correctOptionNumber)
            throws SQLException {
        try (Connection c = Database.getConnection()) {
            try (PreparedStatement del = c.prepareStatement(
                    "DELETE FROM QuizAttemptAnswer WHERE scoreID=? AND questionID=?")) {
                del.setLong(1, scoreId);
                del.setLong(2, questionId);
                del.executeUpdate();
            }
            recordAnswer(scoreId, questionId, userOptionNumber, correctOptionNumber);
        }
    }
}
