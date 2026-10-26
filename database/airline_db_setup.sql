-- ==============================================================
-- Airline Management System - Database Setup Script
-- Database: airline_db
-- Designed for College Java Mini-Project (SE Comp A)
-- ==============================================================

DROP DATABASE IF EXISTS airline_db;
CREATE DATABASE airline_db;
USE airline_db;

-- --------------------------------------------------------------
-- 1. Employees Table
-- Captures employee specialties (Pilot, Cabin Crew, Engineer)
-- reflecting the disjoint specialization from the ER diagram.
-- --------------------------------------------------------------
CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    specialty ENUM('Pilot', 'Cabin Crew', 'Engineer') NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    employment_status ENUM('Active', 'Inactive') NOT NULL DEFAULT 'Active'
);

-- --------------------------------------------------------------
-- 2. Passengers Table
-- Stores passenger profile with unique passport identification.
-- --------------------------------------------------------------
CREATE TABLE passengers (
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    passport_number VARCHAR(50) NOT NULL UNIQUE
);

-- --------------------------------------------------------------
-- 3. Flights Table
-- Manages flight schedules, source/destination airports, and status.
-- --------------------------------------------------------------
CREATE TABLE flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    source_airport VARCHAR(10) NOT NULL,
    destination_airport VARCHAR(10) NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    status ENUM('Scheduled', 'Delayed', 'Cancelled') NOT NULL DEFAULT 'Scheduled'
);

-- --------------------------------------------------------------
-- 4. Reservations Table
-- Relational booking connecting passengers and flights.
-- Enforces:
--  - ON DELETE RESTRICT to protect active reservation integrity.
--  - UNIQUE(flight_id, seat_number) to prevent double-booking.
-- --------------------------------------------------------------
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_id INT NOT NULL,
    flight_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Confirmed', 'Cancelled') NOT NULL DEFAULT 'Confirmed',
    CONSTRAINT fk_reservation_passenger 
        FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id) 
        ON DELETE RESTRICT,
    CONSTRAINT fk_reservation_flight 
        FOREIGN KEY (flight_id) REFERENCES flights(flight_id) 
        ON DELETE RESTRICT,
    CONSTRAINT uq_flight_seat 
        UNIQUE (flight_id, seat_number)
);

-- ==============================================================
-- Sample Seed Data for Demonstration & Viva
-- ==============================================================

-- Seed Employees
INSERT INTO employees (full_name, specialty, contact_number, email, employment_status) VALUES
('Captain Rajesh Sharma', 'Pilot', '+91 98201 12345', 'rajesh.sharma@airsky.com', 'Active'),
('First Officer Priya Nair', 'Pilot', '+91 98202 23456', 'priya.nair@airsky.com', 'Active'),
('Sneha Kulkarni', 'Cabin Crew', '+91 98203 34567', 'sneha.k@airsky.com', 'Active'),
('Vikram Mehta', 'Cabin Crew', '+91 98204 45678', 'vikram.m@airsky.com', 'Active'),
('Anil Verma', 'Engineer', '+91 98205 56789', 'anil.verma@airsky.com', 'Active'),
('Rohan Deshmukh', 'Engineer', '+91 98206 67890', 'rohan.d@airsky.com', 'Inactive');

-- Seed Passengers
INSERT INTO passengers (full_name, email, contact_number, passport_number) VALUES
('Aarav Patel', 'aarav.patel@gmail.com', '+91 99111 22334', 'Z1234567'),
('Diya Sengupta', 'diya.s@yahoo.com', '+91 99222 33445', 'K9876543'),
('Karan Malhotra', 'karan.m@outlook.com', '+91 99333 44556', 'M4567891'),
('Meera Iyer', 'meera.iyer@gmail.com', '+91 99444 55667', 'A7890123');

-- Seed Flights
INSERT INTO flights (flight_number, source_airport, destination_airport, departure_time, arrival_time, status) VALUES
('AI-101', 'BOM', 'DEL', '2026-11-01 06:30:00', '2026-11-01 08:45:00', 'Scheduled'),
('6E-204', 'BLR', 'BOM', '2026-11-01 09:15:00', '2026-11-01 11:00:00', 'Scheduled'),
('UK-825', 'DEL', 'GOI', '2026-11-01 13:00:00', '2026-11-01 15:30:00', 'Delayed'),
('SG-402', 'HYD', 'CCU', '2026-11-01 17:45:00', '2026-11-01 20:10:00', 'Scheduled'),
('AI-312', 'BOM', 'MAA', '2026-11-02 07:00:00', '2026-11-02 09:00:00', 'Cancelled');

-- Seed Reservations
INSERT INTO reservations (passenger_id, flight_id, seat_number, booking_date, status) VALUES
(1, 1, '12A', '2026-10-20 10:15:00', 'Confirmed'),
(2, 1, '12B', '2026-10-21 14:30:00', 'Confirmed'),
(3, 2, '04C', '2026-10-22 16:45:00', 'Confirmed'),
(4, 3, '18D', '2026-10-23 11:20:00', 'Confirmed');
