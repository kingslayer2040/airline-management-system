package com.airline.service;

import com.airline.dao.EmployeeDao;
import com.airline.exception.ApiException;
import com.airline.model.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer implementing business logic and validation for Employee operations.
 */
@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final List<String> ALLOWED_SPECIALTIES = Arrays.asList("Pilot", "Cabin Crew", "Engineer");

    public EmployeeService(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDao.findAll();
    }

    public Employee getEmployeeById(int id) throws SQLException {
        return employeeDao.findById(id)
                .orElseThrow(() -> new ApiException("Employee not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Employee addEmployee(Employee employee) throws SQLException {
        // Validation: Required fields
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            throw new ApiException("Full name is required.");
        }
        if (employee.getSpecialty() == null || !ALLOWED_SPECIALTIES.contains(employee.getSpecialty().trim())) {
            throw new ApiException("Specialty must be one of: Pilot, Cabin Crew, Engineer.");
        }
        if (employee.getContactNumber() == null || employee.getContactNumber().trim().isEmpty()) {
            throw new ApiException("Contact number is required.");
        }
        if (employee.getEmail() == null || !EMAIL_PATTERN.matcher(employee.getEmail().trim()).matches()) {
            throw new ApiException("A valid email address is required (e.g. user@domain.com).");
        }

        // Employment status defaults to 'Active'
        if (employee.getEmploymentStatus() == null || employee.getEmploymentStatus().trim().isEmpty()) {
            employee.setEmploymentStatus("Active");
        } else if (!"Active".equalsIgnoreCase(employee.getEmploymentStatus().trim()) &&
                   !"Inactive".equalsIgnoreCase(employee.getEmploymentStatus().trim())) {
            throw new ApiException("Employment status must be either 'Active' or 'Inactive'.");
        }

        // Clean values
        employee.setFullName(employee.getFullName().trim());
        employee.setSpecialty(employee.getSpecialty().trim());
        employee.setContactNumber(employee.getContactNumber().trim());
        employee.setEmail(employee.getEmail().trim().toLowerCase());

        return employeeDao.save(employee);
    }

    public void deleteEmployee(int id) throws SQLException {
        boolean deleted = employeeDao.deleteById(id);
        if (!deleted) {
            throw new ApiException("Employee not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
