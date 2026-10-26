# ==============================================================
# Automated Verification Script for Airline Management System
# Tests all Spring Boot REST API Endpoints with MySQL Database
# ==============================================================

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Airline Management System - Full API Verification Suite " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"
$global:passed = 0
$global:failed = 0

function Run-Test {
    param (
        [string]$TestName,
        [scriptblock]$Action
    )
    Write-Host "[TEST] $TestName..." -NoNewline
    try {
        & $Action
        Write-Host " [PASS]" -ForegroundColor Green
        $global:passed++
    } catch {
        Write-Host " [FAIL]" -ForegroundColor Red
        Write-Host "       Message: $($_.Exception.Message)" -ForegroundColor Yellow
        $global:failed++
    }
}

# 1. Test Dashboard
Run-Test -TestName "Dashboard API (GET /api/dashboard)" -Action {
    $res = Invoke-RestMethod -Uri "$baseUrl/dashboard" -Method Get
    if ($res.totalEmployees -lt 1) { throw "Invalid employee count" }
    if ($res.totalFlights -lt 1) { throw "Invalid flight count" }
}

# 2. Test Employee Lifecycle
$global:empId = $null
Run-Test -TestName "Employee Creation (POST /api/employees)" -Action {
    $body = @{
        fullName = "Test Pilot John Doe"
        specialty = "Pilot"
        contactNumber = "+91 91234 56789"
        email = "john.doe@testairline.com"
        employmentStatus = "Active"
    } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$baseUrl/employees" -Method Post -Body $body -ContentType "application/json"
    if (-not $created.employeeId) { throw "No employee ID returned" }
    $global:empId = $created.employeeId
}

Run-Test -TestName "Employee Deletion (DELETE /api/employees/{id})" -Action {
    $del = Invoke-RestMethod -Uri "$baseUrl/employees/$global:empId" -Method Delete
    if (-not $del.message) { throw "No confirmation message returned" }
}

# 3. Test Passenger Lifecycle and Duplicate Passport
$global:passId = $null
Run-Test -TestName "Passenger Registration (POST /api/passengers)" -Action {
    $body = @{
        fullName = "Test Passenger Alice"
        email = "alice@testmail.com"
        contactNumber = "+91 99887 76655"
        passportNumber = "TESTPASS99"
    } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$baseUrl/passengers" -Method Post -Body $body -ContentType "application/json"
    if (-not $created.passengerId) { throw "No passenger ID returned" }
    $global:passId = $created.passengerId
}

Run-Test -TestName "Duplicate Passport Rejection (409 Conflict)" -Action {
    $body = @{
        fullName = "Duplicate Alice"
        email = "alice2@testmail.com"
        contactNumber = "+91 99887 76655"
        passportNumber = "TESTPASS99"
    } | ConvertTo-Json
    $rejected = $false
    try {
        Invoke-RestMethod -Uri "$baseUrl/passengers" -Method Post -Body $body -ContentType "application/json"
    } catch {
        $rejected = $true
    }
    if (-not $rejected) { throw "Failed to reject duplicate passport!" }
}

Run-Test -TestName "Passenger Deletion (DELETE /api/passengers/{id})" -Action {
    $del = Invoke-RestMethod -Uri "$baseUrl/passengers/$global:passId" -Method Delete
    if (-not $del.message) { throw "No confirmation message returned" }
}

# 4. Test Flight Lifecycle and Validations
$global:flightId = $null
Run-Test -TestName "Flight Scheduling (POST /api/flights)" -Action {
    $body = @{
        flightNumber = "TEST-101"
        sourceAirport = "DEL"
        destinationAirport = "BOM"
        departureTime = "2026-12-01T10:00:00"
        arrivalTime = "2026-12-01T12:15:00"
        status = "Scheduled"
    } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$baseUrl/flights" -Method Post -Body $body -ContentType "application/json"
    if (-not $created.flightId) { throw "No flight ID returned" }
    $global:flightId = $created.flightId
}

Run-Test -TestName "Identical Origin and Destination Rejection (400 Bad Request)" -Action {
    $body = @{
        flightNumber = "INVALID-1"
        sourceAirport = "BOM"
        destinationAirport = "BOM"
        departureTime = "2026-12-01T10:00:00"
        arrivalTime = "2026-12-01T12:15:00"
        status = "Scheduled"
    } | ConvertTo-Json
    $rejected = $false
    try {
        Invoke-RestMethod -Uri "$baseUrl/flights" -Method Post -Body $body -ContentType "application/json"
    } catch {
        $rejected = $true
    }
    if (-not $rejected) { throw "Failed to reject identical origin and destination!" }
}

Run-Test -TestName "Flight Deletion (DELETE /api/flights/{id})" -Action {
    $del = Invoke-RestMethod -Uri "$baseUrl/flights/$global:flightId" -Method Delete
    if (-not $del.message) { throw "No confirmation message returned" }
}

# 5. Test Reservations and Double Booking Prevention
$global:resId = $null
Run-Test -TestName "Reservation Booking (POST /api/reservations)" -Action {
    $body = @{
        passengerId = 1
        flightId = 1
        seatNumber = "30A"
    } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$baseUrl/reservations" -Method Post -Body $body -ContentType "application/json"
    if (-not $created.reservationId) { throw "No reservation ID returned" }
    $global:resId = $created.reservationId
}

Run-Test -TestName "Double Booking Prevention for Same Seat (409 Conflict)" -Action {
    $body = @{
        passengerId = 2
        flightId = 1
        seatNumber = "30A"
    } | ConvertTo-Json
    $rejected = $false
    try {
        Invoke-RestMethod -Uri "$baseUrl/reservations" -Method Post -Body $body -ContentType "application/json"
    } catch {
        $rejected = $true
    }
    if (-not $rejected) { throw "Failed to reject double booking on the same seat!" }
}

Run-Test -TestName "Reservation Cancellation (DELETE /api/reservations/{id})" -Action {
    $del = Invoke-RestMethod -Uri "$baseUrl/reservations/$global:resId" -Method Delete
    if (-not $del.message) { throw "No confirmation message returned" }
}

Write-Host "----------------------------------------------------------" -ForegroundColor Cyan
Write-Host " Test Summary: $global:passed Passed, $global:failed Failed" -ForegroundColor $(if ($global:failed -eq 0) { "Green" } else { "Red" })
Write-Host "==========================================================" -ForegroundColor Cyan
