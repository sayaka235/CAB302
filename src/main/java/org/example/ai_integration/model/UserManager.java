package org.example.ai_integration.model;

/**
 * Manages the currently logged-in user.
 * <p>
 * Implements a Singleton pattern so there is only one instance
 * accessible across all scenes. Stores the logged-in user and
 * provides methods to check login status or log out.
 */
public class UserManager {
    /** Singleton instance of the UserManager */
    private static UserManager instance;
    /** The currently logged-in user */
    private User loggedInUser;
    /** Private constructor to enforce Singleton pattern */
    private UserManager() {

    }
    /**
     * Gets the single instance of the UserManager.
     * @return the instance of UserManager
     */
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    /**
     * Sets the currently logged-in user.
     * @param user the user who just logged in
     */
    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    /**
     * Gets the currently logged-in user.
     * @return the logged-in user, or null if no one is logged in
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
    /**
     * Checks whether a user is currently logged in.
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return loggedInUser != null;
    }
    /** Logs out the current user by clearing the stored data */
    public void logout() {
        loggedInUser = null; // Clear the user data on logout
    }
}
