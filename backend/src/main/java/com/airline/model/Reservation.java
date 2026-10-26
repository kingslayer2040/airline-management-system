package com.airline.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Model representing a seat booking in the reservations table.
 */
public class Reservation {

    private Integer reservationId;
    private Integer passengerId;
    private Integer flightId;
    private String seatNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm[:ss]")
    private LocalDateTime bookingDate;

    private String status; // 'Confirmed' or 'Cancelled'

    // Default Constructor
    public Reservation() {
    }

    // Parameterized Constructor without ID
    public Reservation(Integer passengerId, Integer flightId, String seatNumber, LocalDateTime bookingDate, String status) {
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Parameterized Constructor with ID
    public Reservation(Integer reservationId, Integer passengerId, Integer flightId, String seatNumber, LocalDateTime bookingDate, String status) {
        this.reservationId = reservationId;
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", passengerId=" + passengerId +
                ", flightId=" + flightId +
                ", seatNumber='" + seatNumber + '\'' +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                '}';
    }
}
