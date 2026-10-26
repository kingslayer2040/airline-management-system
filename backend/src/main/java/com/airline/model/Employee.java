package com.airline.model;

/**
 * Model representing an Airline Employee.
 * Demonstrates OOP concepts: Encapsulation (private fields, public getters/setters),
 * constructors, and domain modeling.
 */
public class Employee {

    private Integer employeeId;
    private String fullName;
    private String specialty; // 'Pilot', 'Cabin Crew', or 'Engineer'
    private String contactNumber;
    private String email;
    private String employmentStatus; // 'Active' or 'Inactive'

    // Default Constructor
    public Employee() {
    }

    // Parameterized Constructor without ID (for insertion)
    public Employee(String fullName, String specialty, String contactNumber, String email, String employmentStatus) {
        this.fullName = fullName;
        this.specialty = specialty;
        this.contactNumber = contactNumber;
        this.email = email;
        this.employmentStatus = employmentStatus;
    }

    // Parameterized Constructor with ID (for reading from DB)
    public Employee(Integer employeeId, String fullName, String specialty, String contactNumber, String email, String employmentStatus) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.specialty = specialty;
        this.contactNumber = contactNumber;
        this.email = email;
        this.employmentStatus = employmentStatus;
    }

    // Getters and Setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", fullName='" + fullName + '\'' +
                ", specialty='" + specialty + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", email='" + email + '\'' +
                ", employmentStatus='" + employmentStatus + '\'' +
                '}';
    }
}
