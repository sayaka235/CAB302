package org.example.ai_integration.model;

public class NoteSummaryManager {
    private static NoteSummaryManager instance;
    private NoteSummary note ;

    public static NoteSummaryManager getInstance() {
        if (instance == null) {
            instance = new NoteSummaryManager();
        }
        return instance;
    }

    public void setNote(NoteSummary note) {
        this.note = note;
    }

    public NoteSummary getCurrentSummaryNote(){return this.note;}
}
