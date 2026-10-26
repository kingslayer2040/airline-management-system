@echo off
title Airline Management System - Spring Boot Backend (Port 8080)
echo ========================================================
echo  Starting Airline Management System Backend (Spring Boot)
echo ========================================================
cd /d "%~dp0backend"

if not exist "target\airline-management-backend-1.0.0.jar" (
    echo [INFO] JAR not found. Building project with Maven...
    call mvnw.cmd clean package -DskipTests
)

echo [INFO] Starting Spring Boot on http://localhost:8080...
java -jar target\airline-management-backend-1.0.0.jar
pause
