package org.example.ai_integration.model;

import java.time.LocalDate;

public class User {
    private String email;
    private String firstname;
    private String lastname;
    private LocalDate dob;
    private String password;

    public User(String email, String firstname, String lastname, LocalDate dob, String password){
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
        this.password = password;
    }

    public String getEmail(){return email;}
    public String getFirst(){return firstname;}
    public String getLast(){return lastname;}
    public LocalDate getDob(){return dob;}
    public String getPassword(){return password;}
    public String getName(){return firstname+ " " +lastname;}
}
