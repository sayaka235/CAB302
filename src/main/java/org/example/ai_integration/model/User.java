package org.example.ai_integration.model;

import java.time.LocalDate;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private LocalDate dob;
    private String password;
    private String userID;

    public User(String email, String firstname, String lastname, LocalDate dob, String password){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
    }
    public User(String email, String firstname, String lastname, LocalDate dob, String password, String userID){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
        this.userID = userID;
    }

    public User(){

    }

    public String getEmail(){return email;}
    public String getFirst(){return firstname;}
    public String getLast(){return lastname;}
    public LocalDate getDob(){return dob;}
    public String getPassword(){return password;}
    public String getName(){return firstname+ " " +lastname;}
    public String getFirstname(){return firstname;}
    public String getUserID(){return userID;}
}
