package com.airline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database configuration providing standard JDBC Connections.
 * Uses PreparedStatement in DAOs to prevent SQL injection.
 * Fully transparent for college viva demonstration of JDBC concepts.
 */
@Configuration
public class DatabaseConfig {

    @Value("${airline.db.url}")
    private String url;

    @Value("${airline.db.username}")
    private String username;

    @Value("${airline.db.password}")
    private String password;

    static {
        try {
            // Load MySQL JDBC Driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found in classpath!", e);
        }
    }

    /**
     * Establishes and returns a new JDBC Connection to MySQL.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
