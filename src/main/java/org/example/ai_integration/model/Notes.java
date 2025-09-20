package org.example.ai_integration.model;

/**
 * Model class for storing note information in the application.
 * <p>
 * Represents a single set of notes with an ID, title, content,
 * and an optional external URL for extended notes.
 */
public class Notes {
    /** Unique ID for the note */
    private long noteID;
    /** Title given to the note */
    private String title;
    /** The main text content of the note */
    private String notes;
    /** Optional external URL linking to additional notes */
    private String extNotesURL;

    /**
     * Creates a new {@code Notes} object with all its attributes.
     *
     * @param noteID the unique identifier for the note
     * @param title the title of the note
     * @param notes the main content of the note
     * @param extNotesURL an external link for extended notes
     */
    public Notes(long noteID, String title, String notes, String extNotesURL) {
        this.noteID = noteID;
        this.title = title;
        this.notes = notes;
        this.extNotesURL = extNotesURL;
    }

    /** @return the unique note ID */
    public long getNoteID() { return noteID; }

    /** @return the title of the note */
    public String getTitle() { return title; }

    /** @return the main text content of the note */
    public String getNotes() { return notes; }

    /** @return the external notes URL (if any) */
    public String getExtNotesURL() { return extNotesURL; }
}
