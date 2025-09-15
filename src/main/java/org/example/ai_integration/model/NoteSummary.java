package org.example.ai_integration.model;

public class NoteSummary {
    private long noteID;
    private String title;
    private String notes;


    public NoteSummary(long noteID, String title, String notes) {
        this.noteID = noteID;
        this.title = title;
        this.notes = notes;

    }

    public long getNoteID() { return noteID; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }

}
