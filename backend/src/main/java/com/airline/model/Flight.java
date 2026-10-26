package com.airline.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Model representing a Flight schedule and route.
 */
public class Flight {

    private Integer flightId;
    private String flightNumber;
    private String sourceAirport;
    private String destinationAirport;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime departureTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime arrivalTime;

    private String status; // 'Scheduled', 'Delayed', or 'Cancelled'

    // Default Constructor
    public Flight() {
    }

    // Parameterized Constructor without ID
    public Flight(String flightNumber, String sourceAirport, String destinationAirport,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
        this.flightNumber = flightNumber;
        this.sourceAirport = sourceAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Parameterized Constructor with ID
    public Flight(Integer flightId, String flightNumber, String sourceAirport, String destinationAirport,
                  LocalDateTime departureTime, LocalDateTime arrivalTime, String status) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.sourceAirport = sourceAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
    }

    // Getters and Setters
    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSourceAirport() {
        return sourceAirport;
    }

    public void setSourceAirport(String sourceAirport) {
        this.sourceAirport = sourceAirport;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "flightId=" + flightId +
                ", flightNumber='" + flightNumber + '\'' +
                ", sourceAirport='" + sourceAirport + '\'' +
                ", destinationAirport='" + destinationAirport + '\'' +
                ", departureTime=" + departureTime +
                ", arrivalTime=" + arrivalTime +
                ", status='" + status + '\'' +
                '}';
    }
}
