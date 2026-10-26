package com.airline.dao;

import com.airline.config.DatabaseConfig;
import com.airline.model.Passenger;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Passengers using JDBC and PreparedStatement.
 */
@Repository
public class PassengerDao {

    private final DatabaseConfig databaseConfig;

    public PassengerDao(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Retrieves all passengers ordered by ID descending.
     */
    public List<Passenger> findAll() throws SQLException {
        List<Passenger> list = new ArrayList<>();
        String sql = "SELECT passenger_id, full_name, email, contact_number, passport_number " +
                     "FROM passengers ORDER BY passenger_id DESC";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPassenger(rs));
            }
        }
        return list;
    }

    /**
     * Finds a passenger by ID.
     */
    public Optional<Passenger> findById(int passengerId) throws SQLException {
        String sql = "SELECT passenger_id, full_name, email, contact_number, passport_number " +
                     "FROM passengers WHERE passenger_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, passengerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPassenger(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Checks if a passport number is already registered in the system.
     */
    public boolean existsByPassport(String passportNumber) throws SQLException {
        String sql = "SELECT 1 FROM passengers WHERE passport_number = ? LIMIT 1";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, passportNumber);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Checks if the passenger has any active reservations.
     * Used for relational integrity check before deletion.
     */
    public boolean hasReservations(int passengerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservations WHERE passenger_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, passengerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Registers a new passenger.
     */
    public Passenger save(Passenger passenger) throws SQLException {
        String sql = "INSERT INTO passengers (full_name, email, contact_number, passport_number) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, passenger.getFullName());
            pstmt.setString(2, passenger.getEmail());
            pstmt.setString(3, passenger.getContactNumber());
            pstmt.setString(4, passenger.getPassportNumber());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating passenger failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    passenger.setPassengerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating passenger failed, no ID obtained.");
                }
            }
        }
        return passenger;
    }

    /**
     * Deletes a passenger by ID.
     */
    public boolean deleteById(int passengerId) throws SQLException {
        String sql = "DELETE FROM passengers WHERE passenger_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, passengerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Passenger mapResultSetToPassenger(ResultSet rs) throws SQLException {
        return new Passenger(
                rs.getInt("passenger_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("contact_number"),
                rs.getString("passport_number")
        );
    }
}
