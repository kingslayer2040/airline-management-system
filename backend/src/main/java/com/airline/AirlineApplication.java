package com.airline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Airline Management System Spring Boot application.
 * Part of the SE Computer Engineering DBMS / Java Mini-Project.
 */
@SpringBootApplication
public class AirlineApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirlineApplication.class, args);
        System.out.println("==================================================");
        System.out.println(" Airline Management System Backend is RUNNING!   ");
        System.out.println(" REST API Base URL: http://localhost:8080/api    ");
        System.out.println("==================================================");
    }
}
