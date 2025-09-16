package org.example.ai_integration.model;

import java.util.ArrayList;
import java.util.List;

public class QuizManager {
    private static QuizManager instance;
    private Quiz currentQuiz = null;
    private final List<Quiz> quizList = new ArrayList<>();

    private QuizManager() {}

    public static QuizManager getInstance() {
        if (instance == null) {
            instance = new QuizManager();
        }
        return instance;
    }

    // Current quiz (selected)
    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    public Quiz getQuiz() {
        return currentQuiz;
    }

    public boolean isQuizSelected() {
        return this.currentQuiz != null;
    }

    public void clearQuiz() {
        this.currentQuiz = null;
        quizList.clear();
    }

    // Multiple quizzes
    public void addQuiz(Quiz quiz) {
        quizList.add(quiz);
    }

    public List<Quiz> getQuizzes() {
        return new ArrayList<>(quizList);
    }
}
