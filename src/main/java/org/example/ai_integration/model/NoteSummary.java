package org.example.ai_integration.model;
/**
 * Model class for storing summarized notes in the application.
 * <p>
 * Represents a note summary with an ID, title, and shortened note content.
 */
public class NoteSummary {
    /** Unique ID for the note summary */
    private long noteID;
    /** Title of the summarized notes */
    private String title;
    /** The summary content of the notes */
    private String notes;

    /**
     * Creates a new {@code NoteSummary} object with its attributes.
     *
     * @param noteID the unique identifier for the note summary
     * @param title the title of the summarized note
     * @param notes the content of the summarized note
     */
    public NoteSummary(long noteID, String title, String notes) {
        this.noteID = noteID;
        this.title = title;
        this.notes = notes;

    }
    /** @return the unique ID of the note summary */
    public long getNoteID() { return noteID; }
    /** @return the title of the note summary */
    public String getTitle() { return title; }
    /** @return the content of the summarized note */
    public String getNotes() { return notes; }

}
