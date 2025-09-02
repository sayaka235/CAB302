package org.example.ai_integration.model;

//Stores the logged-in user statically so that it can be accessed by each scene
public class UserManager {
    private static UserManager instance;
    private User loggedInUser;

    // Private constructor to enforce Singleton pattern
    private UserManager() {
        // Initialize if needed, or leave empty
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public void logout() {
        loggedInUser = null; // Clear the user data on logout
    }
}
