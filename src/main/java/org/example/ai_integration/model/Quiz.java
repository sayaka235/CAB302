package org.example.ai_integration.model;
/**
 * Model class for storing quiz information in the application.
 * <p>
 * Represents a quiz with a unique ID and a title.
 */
public class Quiz {
/** Unique ID for the quiz */
   private long quizID;
    /** Title of the quiz */
   private String title;

    /**
     * Creates a new {@code Quiz} object with its attributes.
     *
     * @param quizID the unique identifier for the quiz
     * @param title the title of the quiz
     */
    public Quiz(long quizID, String title)
    {
        this.quizID = quizID;
        this.title = title;
    }

    /** @return the title of the quiz */
    public String geTitle() {
        return title;
    }

    /** @return the unique quiz ID */
    public long getQuizID() {
        return quizID;
    }
}
