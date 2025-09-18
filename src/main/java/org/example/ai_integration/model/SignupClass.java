package org.example.ai_integration.model;

import java.util.Date;

/**
 * Represents a user signing up with personal details.
 * <p>
 * Stores information such as name, email, address, and date of birth.
 */
public class SignupClass {
    /** The unique identifier of the user */
    private int id;
    /** The first name of the user */
    private String firstName;
    /** The last name of the user */
    private String lastName;
    /** The email of the user */
    private String email;
    /** The address (or phone field, reused) of the user */
    private String address;
    /** The date of birth of the user */
    private Date dob;

    /**
     * Creates a new signup record with the given details.
     *
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     * @param address   the user's address
     * @param dob       the user's date of birth
     */
    public SignupClass(String firstName, String lastName, String email, String address, Date dob) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.dob = dob;
    }

    /** @return the unique identifier of the user */
    public int getId() {
        return id;
    }

    /** @param id sets the unique identifier of the user */
    public void setId(int id) {
        this.id = id;
    }

    /** @return the first name of the user */
    public String getFirstName() {
        return firstName;
    }

    /** @param firstName sets the first name of the user */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /** @return the last name of the user */
    public String getLastName() {
        return lastName;
    }

    /** @param lastName sets the last name of the user */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /** @return the email address of the user */
    public String getEmail() {
        return email;
    }

    /** @param email sets the email address of the user */
    public void setEmail(String email) {
        this.email = email;
    }

    /** @return the date of birth of the user */
    public Date getDob() {
        return dob;
    }

    /** @param dob sets the date of birth of the user */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /** @return the address (or phone field) of the user */
    public String getPhone() {
        return address;
    }

    /** @param phone sets the address (stored in the address field) */
    public void setPhone(String phone) {
        this.address = address;
    }

    /**
     * Combines first and last name into a full name.
     * @return the full name of the user
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
