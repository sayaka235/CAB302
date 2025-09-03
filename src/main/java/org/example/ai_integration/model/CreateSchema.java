package org.example.ai_integration.model;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class CreateSchema {
    private CreateSchema() {}

    public static void initAll() throws SQLException {
        try (Connection c = Database.getConnection(); Statement st = c.createStatement()) {

            // --- Users ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Users(
                  ID           INTEGER PRIMARY KEY,
                  email        TEXT NOT NULL UNIQUE,
                  firstName    TEXT NOT NULL,
                  lastName     TEXT NOT NULL,
                  dob          TEXT NOT NULL,  -- 'YYYY-MM-DD'
                  passwordHash TEXT NOT NULL,
                  createdAt    TEXT NOT NULL DEFAULT (datetime('now'))
                );
            """);

            // --- Quiz (standalone quiz definitions/snapshots) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Quiz(
                  quizID        INTEGER PRIMARY KEY,
                  difficulty    INTEGER NOT NULL,
                  score         INTEGER NOT NULL,
                  numQuestions  INTEGER NOT NULL,
                  numAttempts   INTEGER NOT NULL,
                  numSuccesses  INTEGER NOT NULL
                );
            """);

            // --- Files (owned by a user) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Files(
                  fileID       INTEGER PRIMARY KEY,
                  userID       INTEGER NOT NULL,
                  filePath     TEXT NOT NULL UNIQUE,
                  dateUploaded TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY (userID) REFERENCES Users(ID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- noteSummary (belongs to a user) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS noteSummary(
                  noteID  INTEGER PRIMARY KEY,
                  userID  INTEGER NOT NULL,
                  title   TEXT NOT NULL UNIQUE,
                  notes   TEXT,
                  FOREIGN KEY (userID) REFERENCES Users(ID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- Milestones (per user; composite PK) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Milestones(
                  milestoneID   INTEGER NOT NULL,
                  userID        INTEGER NOT NULL,
                  milestoneName TEXT NOT NULL,
                  dateAchieved  TEXT NOT NULL,
                  PRIMARY KEY (userID, milestoneID),
                  FOREIGN KEY (userID) REFERENCES Users(ID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- UserStats (1:1 with Users) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS UserStats(
                  userID          INTEGER PRIMARY KEY,
                  totalQuizzes    INTEGER,
                  streak          INTEGER,
                  recentScore     INTEGER,
                  longestStreak   INTEGER,
                  improvementRate REAL,
                  FOREIGN KEY (userID) REFERENCES Users(ID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- QuizQuestions (questions belong to a quiz) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS QuizQuestions(
                  questionID          INTEGER PRIMARY KEY,
                  quizID              INTEGER NOT NULL,
                  questionText        TEXT NOT NULL,
                  option1             TEXT NOT NULL,
                  option2             TEXT NOT NULL,
                  option3             TEXT NOT NULL,
                  option4             TEXT NOT NULL,
                  correctOptionNumber INTEGER NOT NULL CHECK (correctOptionNumber BETWEEN 1 AND 4),
                  FOREIGN KEY (quizID) REFERENCES Quiz(quizID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- QuizScoreArray (attempts by a user on a quiz) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS QuizScoreArray(
                  scoreID        INTEGER PRIMARY KEY,
                  quizID         INTEGER NOT NULL,
                  userID         INTEGER NOT NULL,
                  score          INTEGER NOT NULL,
                  improvementRate REAL,
                  dateAttempted  TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY (quizID) REFERENCES Quiz(quizID)
                    ON UPDATE CASCADE ON DELETE CASCADE,
                  FOREIGN KEY (userID) REFERENCES Users(ID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);

            // --- QuizAttemptAnswer (per-question answers for MCQ) ---
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS QuizAttemptAnswer(
                  id               INTEGER PRIMARY KEY,
                  scoreID          INTEGER NOT NULL,
                  questionID       INTEGER NOT NULL,
                  userOptionNumber INTEGER NOT NULL,   -- 1..4
                  isCorrect        INTEGER NOT NULL,   -- 0/1
                  answeredAt       TEXT NOT NULL DEFAULT (datetime('now')),
                  FOREIGN KEY (scoreID)   REFERENCES QuizScoreArray(scoreID)
                    ON UPDATE CASCADE ON DELETE CASCADE,
                  FOREIGN KEY (questionID) REFERENCES QuizQuestions(questionID)
                    ON UPDATE CASCADE ON DELETE CASCADE
                );
            """);
            st.executeUpdate("""
                INSERT OR IGNORE INTO Users(ID, email, firstName, lastName, dob, passwordHash)
                VALUES(1, 'demo@example.com', 'Demo', 'User', '2000-01-01', 'dev-hash')
            """);
        }
    }
}

