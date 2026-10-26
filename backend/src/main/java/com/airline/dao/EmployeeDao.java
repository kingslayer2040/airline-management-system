package com.airline.dao;

import com.airline.config.DatabaseConfig;
import com.airline.model.Employee;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Employees using plain JDBC and PreparedStatement.
 * Perfect for viva demonstration of JDBC concepts.
 */
@Repository
public class EmployeeDao {

    private final DatabaseConfig databaseConfig;

    // Constructor-based Dependency Injection
    public EmployeeDao(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Retrieves all employees from the database ordered by ID descending.
     */
    public List<Employee> findAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, full_name, specialty, contact_number, email, employment_status " +
                     "FROM employees ORDER BY employee_id DESC";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        }
        return list;
    }

    /**
     * Finds an employee by ID.
     */
    public Optional<Employee> findById(int employeeId) throws SQLException {
        String sql = "SELECT employee_id, full_name, specialty, contact_number, email, employment_status " +
                     "FROM employees WHERE employee_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToEmployee(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Inserts a new employee into the database and returns the generated employee_id.
     */
    public Employee save(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (full_name, specialty, contact_number, email, employment_status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, employee.getFullName());
            pstmt.setString(2, employee.getSpecialty());
            pstmt.setString(3, employee.getContactNumber());
            pstmt.setString(4, employee.getEmail());
            pstmt.setString(5, employee.getEmploymentStatus());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setEmployeeId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating employee failed, no ID obtained.");
                }
            }
        }
        return employee;
    }

    /**
     * Deletes an employee by ID. Returns true if a record was deleted.
     */
    public boolean deleteById(int employeeId) throws SQLException {
        String sql = "DELETE FROM employees WHERE employee_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Helper method to map a ResultSet row to an Employee object.
     */
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("full_name"),
                rs.getString("specialty"),
                rs.getString("contact_number"),
                rs.getString("email"),
                rs.getString("employment_status")
        );
    }
}
