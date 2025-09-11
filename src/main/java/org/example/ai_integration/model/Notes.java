package org.example.ai_integration.model;
public class Notes {
    private long noteID;
    private String title;
    private String notes;
    private String extNotesURL;

    public Notes(long noteID, String title, String notes, String extNotesURL) {
        this.noteID = noteID;
        this.title = title;
        this.notes = notes;
        this.extNotesURL = extNotesURL;
    }

    public long getNoteID() { return noteID; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public String getExtNotesURL() { return extNotesURL; }
}
