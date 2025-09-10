package org.example.ai_integration.model;

public class Quiz {
   private long quizID;
   private String title;
    public Quiz(long quizID, String title)
    {
        this.quizID = quizID;
        this.title = title;
    }

    public String geTitle() {
        return title;
    }

    public long getQuizID() {
        return quizID;
    }
}
