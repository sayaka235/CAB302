package org.example.ai_integration.model;

import java.time.LocalDate;
/**
 * Represents a user of the application.
 * <p>
 * Stores details such as email, name, date of birth, password,
 * and the unique user ID assigned in the database.
 */
public class User {
    /** The email of the user */
    private String email;
    /** The first name of the user */
    private String firstname;
    /** The last name of the user */
    private String lastname;
    /** The date of birth of the user */
    private LocalDate dob;
    /** The password (hashed) of the user */
    private String password;
    /** The unique identifier for the user in the database */
    private String userID;

    /**
     * Creates a user without an assigned ID.
     * @param email the email of the user
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param dob the date of birth of the user
     * @param password the password of the user
     */
    public User(String email, String firstname, String lastname, LocalDate dob, String password){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
    }
    /**
     * Creates a user with a database ID.
     * @param email the email of the user
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param dob the date of birth of the user
     * @param password the password of the user
     * @param userID the database ID of the user
     */
    public User(String email, String firstname, String lastname, LocalDate dob, String password, String userID){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
        this.userID = userID;
    }
    /** Default constructor */
    public User(){}

    /** @return the email of the user */
    public String getEmail(){ return email; }

    /** @return the first name of the user */
    public String getFirst(){ return firstname; }

    /** @return the last name of the user */
    public String getLast(){ return lastname; }

    /** @return the date of birth of the user */
    public LocalDate getDob(){ return dob; }

    /** @return the password of the user */
    public String getPassword(){ return password; }

    /** @return the full name of the user */
    public String getName(){ return firstname + " " + lastname; }

    /** @return the first name of the user */
    public String getFirstname(){ return firstname; }

    /** @return the unique database ID of the user */
    public String getUserID(){ return userID; }
}
