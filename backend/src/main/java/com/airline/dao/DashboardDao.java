package com.airline.dao;

import com.airline.config.DatabaseConfig;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Dashboard aggregate statistics.
 * Demonstrates SQL COUNT() aggregate functions.
 */
@Repository
public class DashboardDao {

    private final DatabaseConfig databaseConfig;

    public DashboardDao(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public long countEmployees() throws SQLException {
        return executeCountQuery("SELECT COUNT(*) FROM employees");
    }

    public long countPassengers() throws SQLException {
        return executeCountQuery("SELECT COUNT(*) FROM passengers");
    }

    public long countFlights() throws SQLException {
        return executeCountQuery("SELECT COUNT(*) FROM flights");
    }

    public long countReservations() throws SQLException {
        return executeCountQuery("SELECT COUNT(*) FROM reservations");
    }

    private long executeCountQuery(String sql) throws SQLException {
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }
}
