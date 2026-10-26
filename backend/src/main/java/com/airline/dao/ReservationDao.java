package com.airline.dao;

import com.airline.config.DatabaseConfig;
import com.airline.model.Reservation;
import com.airline.model.ReservationView;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservations using JDBC and PreparedStatement.
 * Uses SQL JOIN queries to retrieve passenger and flight details for the Admin table.
 */
@Repository
public class ReservationDao {

    private final DatabaseConfig databaseConfig;

    public ReservationDao(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    /**
     * Retrieves all reservations joined with passenger and flight details.
     */
    public List<ReservationView> findAllWithDetails() throws SQLException {
        List<ReservationView> list = new ArrayList<>();
        String sql = "SELECT r.reservation_id, r.passenger_id, p.full_name AS passenger_name, p.email AS passenger_email, " +
                     "       r.flight_id, f.flight_number, CONCAT(f.source_airport, ' -> ', f.destination_airport) AS route, " +
                     "       r.seat_number, r.booking_date, r.status " +
                     "FROM reservations r " +
                     "JOIN passengers p ON r.passenger_id = p.passenger_id " +
                     "JOIN flights f ON r.flight_id = f.flight_id " +
                     "ORDER BY r.reservation_id DESC";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new ReservationView(
                        rs.getInt("reservation_id"),
                        rs.getInt("passenger_id"),
                        rs.getString("passenger_name"),
                        rs.getString("passenger_email"),
                        rs.getInt("flight_id"),
                        rs.getString("flight_number"),
                        rs.getString("route"),
                        rs.getString("seat_number"),
                        rs.getTimestamp("booking_date").toLocalDateTime(),
                        rs.getString("status")
                ));
            }
        }
        return list;
    }

    /**
     * Checks if a specific seat is already booked on a given flight.
     */
    public boolean isSeatBooked(int flightId, String seatNumber) throws SQLException {
        String sql = "SELECT 1 FROM reservations WHERE flight_id = ? AND UPPER(seat_number) = UPPER(?) LIMIT 1";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, flightId);
            pstmt.setString(2, seatNumber.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Inserts a new reservation.
     */
    public Reservation save(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (passenger_id, flight_id, seat_number, booking_date, status) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservation.getPassengerId());
            pstmt.setInt(2, reservation.getFlightId());
            pstmt.setString(3, reservation.getSeatNumber().trim().toUpperCase());

            LocalDateTime bookingDate = reservation.getBookingDate() != null 
                    ? reservation.getBookingDate() 
                    : LocalDateTime.now();
            reservation.setBookingDate(bookingDate);

            pstmt.setTimestamp(4, Timestamp.valueOf(bookingDate));
            pstmt.setString(5, reservation.getStatus() != null ? reservation.getStatus() : "Confirmed");

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setReservationId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
        }
        return reservation;
    }

    /**
     * Deletes a reservation by ID.
     */
    public boolean deleteById(int reservationId) throws SQLException {
        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
