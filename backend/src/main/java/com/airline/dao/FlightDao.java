package com.airline.dao;

import com.airline.config.DatabaseConfig;
import com.airline.model.Flight;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Flights using JDBC and PreparedStatement.
 */
@Repository
public class FlightDao {

    private final DatabaseConfig databaseConfig;

    public FlightDao(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Retrieves all flights ordered by departure time descending.
     */
    public List<Flight> findAll() throws SQLException {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT flight_id, flight_number, source_airport, destination_airport, " +
                     "departure_time, arrival_time, status FROM flights ORDER BY departure_time DESC";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToFlight(rs));
            }
        }
        return list;
    }

    /**
     * Finds a flight by ID.
     */
    public Optional<Flight> findById(int flightId) throws SQLException {
        String sql = "SELECT flight_id, flight_number, source_airport, destination_airport, " +
                     "departure_time, arrival_time, status FROM flights WHERE flight_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFlight(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves the latest N flights for the dashboard summary table.
     */
    public List<Flight> findRecentFlights(int limit) throws SQLException {
        List<Flight> list = new ArrayList<>();
        String sql = "SELECT flight_id, flight_number, source_airport, destination_airport, " +
                     "departure_time, arrival_time, status FROM flights ORDER BY departure_time DESC LIMIT ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlight(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if any reservations reference this flight.
     */
    public boolean hasReservations(int flightId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE flight_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Adds a new flight schedule.
     */
    public Flight save(Flight flight) throws SQLException {
        String sql = "INSERT INTO flights (flight_number, source_airport, destination_airport, " +
                     "departure_time, arrival_time, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, flight.getFlightNumber().toUpperCase());
            pstmt.setString(2, flight.getSourceAirport().toUpperCase());
            pstmt.setString(3, flight.getDestinationAirport().toUpperCase());
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setString(6, flight.getStatus() != null ? flight.getStatus() : "Scheduled");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating flight failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    flight.setFlightId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating flight failed, no ID obtained.");
                }
            }
        }
        return flight;
    }

    /**
     * Deletes a flight by ID.
     */
    public boolean deleteById(int flightId) throws SQLException {
        String sql = "DELETE FROM flights WHERE flight_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Flight mapResultSetToFlight(ResultSet rs) throws SQLException {
        return new Flight(
                rs.getInt("flight_id"),
                rs.getString("flight_number"),
                rs.getString("source_airport"),
                rs.getString("destination_airport"),
                rs.getTimestamp("departure_time").toLocalDateTime(),
                rs.getTimestamp("arrival_time").toLocalDateTime(),
                rs.getString("status")
        );
    }
}
