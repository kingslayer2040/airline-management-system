package com.airline.controller;

import com.airline.model.Flight;
import com.airline.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Flight and Route Management endpoints.
 */
@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() throws SQLException {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable int id) throws SQLException {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping
    public ResponseEntity<Flight> addFlight(@RequestBody Flight flight) throws SQLException {
        Flight created = flightService.addFlight(flight);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFlight(@PathVariable int id) throws SQLException {
        flightService.deleteFlight(id);
        return ResponseEntity.ok(Collections.singletonMap("message", "Flight with ID " + id + " was deleted successfully."));
    }
}
