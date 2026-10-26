package com.airline.model;

import java.util.List;

/**
 * Model representing the aggregated metrics and recent flights for the Admin Dashboard.
 */
public class DashboardStats {

    private long totalEmployees;
    private long totalPassengers;
    private long totalFlights;
    private long totalReservations;
    private List<Flight> recentFlights;

    public DashboardStats() {
    }

    public DashboardStats(long totalEmployees, long totalPassengers, long totalFlights, long totalReservations, List<Flight> recentFlights) {
        this.totalEmployees = totalEmployees;
        this.totalPassengers = totalPassengers;
        this.totalFlights = totalFlights;
        this.totalReservations = totalReservations;
        this.recentFlights = recentFlights;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(long totalPassengers) {
        this.totalPassengers = totalPassengers;
    }

    public long getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(long totalFlights) {
        this.totalFlights = totalFlights;
    }

    public long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(long totalReservations) {
        this.totalReservations = totalReservations;
    }

    public List<Flight> getRecentFlights() {
        return recentFlights;
    }

    public void setRecentFlights(List<Flight> recentFlights) {
        this.recentFlights = recentFlights;
    }
}
