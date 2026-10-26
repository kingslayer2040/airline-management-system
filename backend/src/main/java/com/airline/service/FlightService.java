package com.airline.service;

import com.airline.dao.FlightDao;
import com.airline.exception.ApiException;
import com.airline.model.Flight;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Service layer implementing business logic and validation for Flight and Route operations.
 */
@Service
public class FlightService {

    private final FlightDao flightDao;
    private static final List<String> ALLOWED_STATUSES = Arrays.asList("Scheduled", "Delayed", "Cancelled");

    public FlightService(FlightDao flightDao) {
        this.flightDao = flightDao;
    }

    public List<Flight> getAllFlights() throws SQLException {
        return flightDao.findAll();
    }

    public Flight getFlightById(int id) throws SQLException {
        return flightDao.findById(id)
                .orElseThrow(() -> new ApiException("Flight not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Flight addFlight(Flight flight) throws SQLException {
        // Validation: Required fields
        if (flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            throw new ApiException("Flight number is required (e.g. AI-101).");
        }
        if (flight.getSourceAirport() == null || flight.getSourceAirport().trim().isEmpty()) {
            throw new ApiException("Source airport code is required (e.g. BOM).");
        }
        if (flight.getDestinationAirport() == null || flight.getDestinationAirport().trim().isEmpty()) {
            throw new ApiException("Destination airport code is required (e.g. DEL).");
        }
        if (flight.getDepartureTime() == null) {
            throw new ApiException("Departure date and time is required.");
        }
        if (flight.getArrivalTime() == null) {
            throw new ApiException("Arrival date and time is required.");
        }

        // Rule: Source and Destination cannot be identical
        if (flight.getSourceAirport().trim().equalsIgnoreCase(flight.getDestinationAirport().trim())) {
            throw new ApiException("Source and destination airports cannot be identical.");
        }

        // Rule: Arrival must be later than departure
        if (!flight.getArrivalTime().isAfter(flight.getDepartureTime())) {
            throw new ApiException("Arrival date and time must be later than departure date and time.");
        }

        // Status validation
        if (flight.getStatus() == null || flight.getStatus().trim().isEmpty()) {
            flight.setStatus("Scheduled");
        } else if (!ALLOWED_STATUSES.contains(flight.getStatus().trim())) {
            throw new ApiException("Flight status must be one of: Scheduled, Delayed, Cancelled.");
        }

        flight.setFlightNumber(flight.getFlightNumber().trim().toUpperCase());
        flight.setSourceAirport(flight.getSourceAirport().trim().toUpperCase());
        flight.setDestinationAirport(flight.getDestinationAirport().trim().toUpperCase());
        flight.setStatus(flight.getStatus().trim());

        return flightDao.save(flight);
    }

    public void deleteFlight(int id) throws SQLException {
        // Check if reservations exist for this flight
        if (flightDao.hasReservations(id)) {
            throw new ApiException("Cannot delete flight: Active passenger reservations exist for this flight.", HttpStatus.BAD_REQUEST);
        }

        boolean deleted = flightDao.deleteById(id);
        if (!deleted) {
            throw new ApiException("Flight not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
