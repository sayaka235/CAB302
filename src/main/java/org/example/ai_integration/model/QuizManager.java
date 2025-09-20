package org.example.ai_integration.model;

import java.util.ArrayList;
import java.util.List;
/**
 * Manager class for handling quiz state across the application.
 * <p>
 * Provides a singleton instance that stores the currently
 * selected quiz and a list of multiple quizzes. This allows
 * different controllers to access and update quiz data easily.
 */
public class QuizManager {
    /** Singleton instance of the QuizManager */
    private static QuizManager instance;
    /** The currently selected quiz */
    private Quiz currentQuiz = null;
    /** List of all quizzes loaded into memory */
    private final List<Quiz> quizList = new ArrayList<>();

    /** Private constructor to enforce singleton pattern */
    private QuizManager() {}

    /**
     * Returns the singleton instance of the QuizManager.
     * Creates a new one if none exists.
     * @return the singleton instance
     */
    public static QuizManager getInstance() {
        if (instance == null) {
            instance = new QuizManager();
        }
        return instance;
    }

    /**
     * Sets the currently selected quiz.
     * @param quiz the quiz to be set as current
     */
    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }
    /**
     * Returns the currently selected quiz.
     * @return the selected quiz, or {@code null} if none is selected
     */
    public Quiz getQuiz() {
        return currentQuiz;
    }
    /**
     * Checks if a quiz is currently selected.
     * @return {@code true} if a quiz is selected, {@code false} otherwise
     */
    public boolean isQuizSelected() {
        return this.currentQuiz != null;
    }

    /**
     * Clears the current quiz and empties the quiz list.
     */
    public void clearQuiz() {
        this.currentQuiz = null;
        quizList.clear();
    }

    /**
     * Adds a quiz to the quiz list.
     * @param quiz the quiz to be added
     */
    public void addQuiz(Quiz quiz) {
        quizList.add(quiz);
    }
    /**
     * Returns a copy of the quiz list.
     * @return a list of quizzes
     */
    public List<Quiz> getQuizzes() {
        return new ArrayList<>(quizList);
    }
}
