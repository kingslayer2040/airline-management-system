@echo off
title Airline Management System - React Frontend (Port 5173)
echo ========================================================
echo  Starting Airline Management System Frontend (React)
echo ========================================================
cd /d "%~dp0frontend"

if not exist "node_modules" (
    echo [INFO] Installing npm dependencies...
    call npm install
)

echo [INFO] Starting Vite development server on http://localhost:5173...
call npm run dev
pause
