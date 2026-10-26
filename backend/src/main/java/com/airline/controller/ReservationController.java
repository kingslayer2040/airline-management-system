package com.airline.controller;

import com.airline.model.Reservation;
import com.airline.model.ReservationView;
import com.airline.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Reservation Management endpoints.
 */
@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationView>> getAllReservations() throws SQLException {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) throws SQLException {
        Reservation created = reservationService.createReservation(reservation);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteReservation(@PathVariable int id) throws SQLException {
        reservationService.deleteReservation(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Reservation with ID " + id + " was cancelled successfully."));
    }
}
