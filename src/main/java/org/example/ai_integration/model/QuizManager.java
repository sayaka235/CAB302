package org.example.ai_integration.model;

//Stores the logged-in user statically so that it can be accessed by each scene
public class QuizManager {
    private static QuizManager instance;
    private Quiz currentQuiz = null;

    // Private constructor to enforce Singleton pattern
    private QuizManager() {
        // Initialize if needed, or leave empty
    }

    public static QuizManager getInstance() {
        if (instance == null) {
            instance = new QuizManager();
        }
        return instance;
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    public Quiz getQuiz() {
        return currentQuiz;
    }

    public void clearQuiz(){this.currentQuiz = null;}

    public boolean isQuizSelected(){return this.currentQuiz!=null;}
}
