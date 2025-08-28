package org.example.ai_integration.model;

import java.util.Date;

public class SignupClass {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private Date dob;

    public SignupClass(String firstName, String lastName, String email, String address, Date dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.dob = dob;
    }

    public int getId() {
        return id;
    }
    /*
    public String testEmailLength(String email){
        return this.email.length();
    }*/

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Date getDob(){
        return dob;
    }
    public void setDob(Date dob){
        this.dob = dob;
    }

    public String getPhone() {
        return address;
    }

    public void setPhone(String phone) {
        this.address = address;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
