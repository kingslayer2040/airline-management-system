package com.airline.service;

import com.airline.dao.PassengerDao;
import com.airline.exception.ApiException;
import com.airline.model.Passenger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service layer implementing business logic and validation for Passenger operations.
 */
@Service
public class PassengerService {

    private final PassengerDao passengerDao;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public PassengerService(PassengerDao passengerDao) {
        this.passengerDao = passengerDao;
    }

    public List<Passenger> getAllPassengers() throws SQLException {
        return passengerDao.findAll();
    }

    public Passenger getPassengerById(int id) throws SQLException {
        return passengerDao.findById(id)
                .orElseThrow(() -> new ApiException("Passenger not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Passenger addPassenger(Passenger passenger) throws SQLException {
        // Validation: Required fields
        if (passenger.getFullName() == null || passenger.getFullName().trim().isEmpty()) {
            throw new ApiException("Full name is required.");
        }
        if (passenger.getEmail() == null || !EMAIL_PATTERN.matcher(passenger.getEmail().trim()).matches()) {
            throw new ApiException("A valid email address is required.");
        }
        if (passenger.getContactNumber() == null || passenger.getContactNumber().trim().isEmpty()) {
            throw new ApiException("Contact number is required.");
        }
        if (passenger.getPassportNumber() == null || passenger.getPassportNumber().trim().isEmpty()) {
            throw new ApiException("Passport number is required.");
        }

        String passportClean = passenger.getPassportNumber().trim().toUpperCase();

        // Check for duplicate passport number
        if (passengerDao.existsByPassport(passportClean)) {
            throw new ApiException("A passenger with passport number '" + passportClean + "' already exists.", HttpStatus.CONFLICT);
        }

        passenger.setFullName(passenger.getFullName().trim());
        passenger.setEmail(passenger.getEmail().trim().toLowerCase());
        passenger.setContactNumber(passenger.getContactNumber().trim());
        passenger.setPassportNumber(passportClean);

        return passengerDao.save(passenger);
    }

    public void deletePassenger(int id) throws SQLException {
        // Check if passenger has active reservations before deletion
        if (passengerDao.hasReservations(id)) {
            throw new ApiException("Cannot delete passenger: They have active flight reservations.", HttpStatus.BAD_REQUEST);
        }

        boolean deleted = passengerDao.deleteById(id);
        if (!deleted) {
            throw new ApiException("Passenger not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
