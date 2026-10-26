package com.airline.service;

import com.airline.dao.DashboardDao;
import com.airline.dao.FlightDao;
import com.airline.model.DashboardStats;
import com.airline.model.Flight;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Service aggregating counts and recent activity for the Admin Dashboard.
 */
@Service
public class DashboardService {

    private final DashboardDao dashboardDao;
    private final FlightDao flightDao;

    public DashboardService(DashboardDao dashboardDao, FlightDao flightDao) {
        this.dashboardDao = dashboardDao;
        this.flightDao = flightDao;
    }

    public DashboardStats getDashboardStats() throws SQLException {
        long employees = dashboardDao.countEmployees();
        long passengers = dashboardDao.countPassengers();
        long flights = dashboardDao.countFlights();
        long reservations = dashboardDao.countReservations();
        List<Flight> recentFlights = flightDao.findRecentFlights(5);

        return new DashboardStats(employees, passengers, flights, reservations, recentFlights);
    }
}
