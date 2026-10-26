@echo off
title Airline Management System - Master Launcher
echo ========================================================
echo  Launching Airline Management System (Full Stack)
echo ========================================================

echo [1/3] Starting Spring Boot Backend in a new window...
start "Airline Backend (8080)" cmd /k "%~dp0start_backend.bat"

echo [2/3] Waiting 4 seconds for backend initialization...
timeout /t 4 /nobreak >nul

echo [3/3] Starting React Frontend in a new window...
start "Airline Frontend (5173)" cmd /k "%~dp0start_frontend.bat"

echo.
echo ========================================================
echo  System is running!
echo  Backend API:  http://localhost:8080/api
echo  Frontend UI:  http://localhost:5173
echo ========================================================
echo.
pause
