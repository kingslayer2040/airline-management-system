package com.airline.service;

import com.airline.dao.FlightDao;
import com.airline.dao.PassengerDao;
import com.airline.dao.ReservationDao;
import com.airline.exception.ApiException;
import com.airline.model.Flight;
import com.airline.model.Passenger;
import com.airline.model.Reservation;
import com.airline.model.ReservationView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer implementing business logic and validation for Reservation operations.
 */
@Service
public class ReservationService {

    private final ReservationDao reservationDao;
    private final PassengerDao passengerDao;
    private final FlightDao flightDao;

    public ReservationService(ReservationDao reservationDao, PassengerDao passengerDao, FlightDao flightDao) {
        this.reservationDao = reservationDao;
        this.passengerDao = passengerDao;
        this.flightDao = flightDao;
    }

    public List<ReservationView> getAllReservations() throws SQLException {
        return reservationDao.findAllWithDetails();
    }

    public Reservation createReservation(Reservation reservation) throws SQLException {
        if (reservation.getPassengerId() == null) {
            throw new ApiException("Please select an existing passenger.");
        }
        if (reservation.getFlightId() == null) {
            throw new ApiException("Please select an existing flight.");
        }
        if (reservation.getSeatNumber() == null || reservation.getSeatNumber().trim().isEmpty()) {
            throw new ApiException("Seat number is required (e.g. 12A).");
        }

        String seatClean = reservation.getSeatNumber().trim().toUpperCase();

        // 1. Verify passenger exists
        Passenger passenger = passengerDao.findById(reservation.getPassengerId())
                .orElseThrow(() -> new ApiException("Selected passenger does not exist in the database.", HttpStatus.NOT_FOUND));

        // 2. Verify flight exists
        Flight flight = flightDao.findById(reservation.getFlightId())
                .orElseThrow(() -> new ApiException("Selected flight does not exist in the database.", HttpStatus.NOT_FOUND));

        // 3. Rule: Prevent duplicate seat bookings on the same flight
        if (reservationDao.isSeatBooked(reservation.getFlightId(), seatClean)) {
            throw new ApiException("Seat " + seatClean + " is already reserved on flight " + flight.getFlightNumber() + ".", HttpStatus.CONFLICT);
        }

        reservation.setSeatNumber(seatClean);
        if (reservation.getBookingDate() == null) {
            reservation.setBookingDate(LocalDateTime.now());
        }
        reservation.setStatus("Confirmed");

        return reservationDao.save(reservation);
    }

    public void deleteReservation(int id) throws SQLException {
        boolean deleted = reservationDao.deleteById(id);
        if (!deleted) {
            throw new ApiException("Reservation not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
