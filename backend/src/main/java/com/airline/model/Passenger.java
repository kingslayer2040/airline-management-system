package com.airline.model;

/**
 * Model representing a Passenger.
 * Encapsulates passenger identity, contact info, and passport number.
 */
public class Passenger {

    private Integer passengerId;
    private String fullName;
    private String email;
    private String contactNumber;
    private String passportNumber;

    // Default Constructor
    public Passenger() {
    }

    // Parameterized Constructor without ID
    public Passenger(String fullName, String email, String contactNumber, String passportNumber) {
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.passportNumber = passportNumber;
    }

    // Parameterized Constructor with ID
    public Passenger(Integer passengerId, String fullName, String email, String contactNumber, String passportNumber) {
        this.passengerId = passengerId;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.passportNumber = passportNumber;
    }

    // Getters and Setters
    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", passportNumber='" + passportNumber + '\'' +
                '}';
    }
}
