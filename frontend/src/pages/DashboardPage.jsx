import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import StatCard from '../components/StatCard';

export default function DashboardPage({ setActivePage, setNotification }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.getDashboard();
      setStats(data);
    } catch (err) {
      setError(err.message || 'Failed to connect to backend server.');
      setNotification({ message: 'Error loading dashboard statistics: ' + err.message, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const formatDateTime = (isoStr) => {
    if (!isoStr) return 'N/A';
    try {
      const d = new Date(isoStr);
      return d.toLocaleString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return isoStr;
    }
  };

  const getStatusBadge = (status) => {
    const s = (status || '').toLowerCase();
    if (s === 'scheduled') return <span className="badge badge-success">Scheduled</span>;
    if (s === 'delayed') return <span className="badge badge-warning">Delayed</span>;
    if (s === 'cancelled') return <span className="badge badge-danger">Cancelled</span>;
    return <span className="badge">{status}</span>;
  };

  if (loading) {
    return (
      <div className="page-container">
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Connecting to MySQL database & loading stats...</p>
        </div>
      </div>
    );
  }

  if (error && !stats) {
    return (
      <div className="page-container">
        <div className="error-card">
          <h3>Failed to Load Dashboard</h3>
          <p>{error}</p>
          <button className="btn btn-primary" onClick={fetchDashboardData}>Retry Connection</button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h2>System Overview & Operations</h2>
          <p className="page-subtitle">Real-time statistics fetched via Spring Boot & JDBC</p>
        </div>
        <button className="btn btn-secondary" onClick={fetchDashboardData}>
          🔄 Refresh Data
        </button>
      </div>

      {/* Summary Cards */}
      <div className="stat-grid">
        <StatCard
          title="Total Employees"
          value={stats ? stats.totalEmployees : 0}
          icon="👨‍✈️"
          color="navy"
          subtitle="Pilots, Crew & Engineers"
        />
        <StatCard
          title="Total Passengers"
          value={stats ? stats.totalPassengers : 0}
          icon="👥"
          color="blue"
          subtitle="Registered travelers"
        />
        <StatCard
          title="Total Flights"
          value={stats ? stats.totalFlights : 0}
          icon="✈️"
          color="sky"
          subtitle="Active flight schedules"
        />
        <StatCard
          title="Total Reservations"
          value={stats ? stats.totalReservations : 0}
          icon="🎫"
          color="indigo"
          subtitle="Confirmed seat bookings"
        />
      </div>

      {/* Quick Action Shortcuts */}
      <div className="quick-actions-bar">
        <span className="quick-label">Quick Actions:</span>
        <button className="btn btn-outline" onClick={() => setActivePage('employees')}>+ Add Employee</button>
        <button className="btn btn-outline" onClick={() => setActivePage('passengers')}>+ Register Passenger</button>
        <button className="btn btn-outline" onClick={() => setActivePage('flights')}>+ Add Flight</button>
        <button className="btn btn-outline" onClick={() => setActivePage('reservations')}>+ Book Seat</button>
      </div>

      {/* Recent Flights Table */}
      <div className="content-card">
        <div className="card-header">
          <div className="card-header-title">
            <h3>Recent Flight Schedules</h3>
            <span className="record-count">{stats?.recentFlights?.length || 0} recent routes</span>
          </div>
          <button className="btn btn-link" onClick={() => setActivePage('flights')}>
            Manage All Flights &rarr;
          </button>
        </div>

        {stats?.recentFlights?.length === 0 ? (
          <div className="empty-state">
            <p>No flight schedules found in the database.</p>
            <button className="btn btn-primary" onClick={() => setActivePage('flights')}>Add Your First Flight</button>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Flight No.</th>
                  <th>Origin</th>
                  <th>Destination</th>
                  <th>Departure Time</th>
                  <th>Arrival Time</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {stats?.recentFlights?.map((flight) => (
                  <tr key={flight.flightId}>
                    <td><strong className="flight-code">{flight.flightNumber}</strong></td>
                    <td><span className="airport-tag">{flight.sourceAirport}</span></td>
                    <td><span className="airport-tag">{flight.destinationAirport}</span></td>
                    <td>{formatDateTime(flight.departureTime)}</td>
                    <td>{formatDateTime(flight.arrivalTime)}</td>
                    <td>{getStatusBadge(flight.status)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
