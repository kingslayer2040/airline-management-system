package com.airline.controller;

import com.airline.model.Passenger;
import com.airline.service.PassengerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Passenger Management endpoints.
 */
@RestController
@RequestMapping("/api/passengers")
@CrossOrigin(origins = "*")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @GetMapping
    public ResponseEntity<List<Passenger>> getAllPassengers() throws SQLException {
        return ResponseEntity.ok(passengerService.getAllPassengers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(passengerService.getPassengerById(id));
    }

    @PostMapping
    public ResponseEntity<Passenger> registerPassenger(@RequestBody Passenger passenger) throws SQLException {
        Passenger created = passengerService.addPassenger(passenger);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePassenger(@PathVariable int id) throws SQLException {
        passengerService.deletePassenger(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Passenger with ID " + id + " was deleted successfully."));
    }
}
