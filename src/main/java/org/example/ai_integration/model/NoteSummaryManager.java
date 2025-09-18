package org.example.ai_integration.model;

/**
 * Singleton manager class for handling the current {@link NoteSummary}.
 * <p>
 * Stores and provides access to the note summary that is currently
 * being viewed or worked on in the application.
 */
public class NoteSummaryManager {
    /** The single instance of this manager (Singleton pattern) */
    private static NoteSummaryManager instance;
    /** The current note summary being managed */
    private NoteSummary note;

    /**
     * Gets the single instance of the {@code NoteSummaryManager}.
     * <p>
     * If no instance exists yet, a new one is created.
     *
     * @return the single {@code NoteSummaryManager} instance
     */
    public static NoteSummaryManager getInstance() {
        if (instance == null) {
            instance = new NoteSummaryManager();
        }
        return instance;
    }

    /**
     * Sets the current note summary to manage.
     *
     * @param note the {@link NoteSummary} to store
     */
    public void setNote(NoteSummary note) {
        this.note = note;
    }

    /**
     * Gets the currently stored note summary.
     *
     * @return the current {@link NoteSummary}, or {@code null} if none is set
     */
    public NoteSummary getCurrentSummaryNote() {
        return this.note;
    }
}
