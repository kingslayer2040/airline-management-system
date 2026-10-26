import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';

export default function FlightsPage({ setNotification }) {
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    flightNumber: '',
    sourceAirport: '',
    destinationAirport: '',
    departureTime: '',
    arrivalTime: '',
    status: 'Scheduled'
  });

  // Modal delete target
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchFlights = async () => {
    try {
      setLoading(true);
      const data = await api.getFlights();
      setFlights(data);
    } catch (err) {
      setNotification({ message: 'Failed to load flights: ' + err.message, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFlights();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    // Client-side validations
    if (!formData.flightNumber.trim()) {
      setNotification({ message: 'Flight number is required (e.g. AI-101).', type: 'error' });
      return;
    }
    if (!formData.sourceAirport.trim() || !formData.destinationAirport.trim()) {
      setNotification({ message: 'Source and Destination airports are required.', type: 'error' });
      return;
    }

    if (formData.sourceAirport.trim().toUpperCase() === formData.destinationAirport.trim().toUpperCase()) {
      setNotification({ message: 'Source and Destination airports cannot be identical.', type: 'error' });
      return;
    }

    if (!formData.departureTime || !formData.arrivalTime) {
      setNotification({ message: 'Departure and Arrival dates and times are required.', type: 'error' });
      return;
    }

    const depDate = new Date(formData.departureTime);
    const arrDate = new Date(formData.arrivalTime);

    if (arrDate <= depDate) {
      setNotification({ message: 'Arrival time must be strictly later than departure time.', type: 'error' });
      return;
    }

    try {
      setSubmitting(true);
      const created = await api.createFlight(formData);
      setNotification({ message: `Flight "${created.flightNumber}" (${created.sourceAirport} ➔ ${created.destinationAirport}) scheduled successfully!`, type: 'success' });

      // Reset form
      setFormData({
        flightNumber: '',
        sourceAirport: '',
        destinationAirport: '',
        departureTime: '',
        arrivalTime: '',
        status: 'Scheduled'
      });
      setIsFormOpen(false);

      fetchFlights();
    } catch (err) {
      setNotification({ message: err.message || 'Error scheduling flight.', type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteFlight(deleteTarget.flightId);
      setNotification({ message: `Flight "${deleteTarget.flightNumber}" deleted successfully.`, type: 'success' });
      setDeleteTarget(null);
      fetchFlights();
    } catch (err) {
      setNotification({ message: err.message || 'Error deleting flight.', type: 'error' });
      setDeleteTarget(null);
    }
  };

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

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h2>Flight & Route Management</h2>
          <p className="page-subtitle">Add flight routes, departure/arrival schedules, and operational status</p>
        </div>
        <button
          className={`btn ${isFormOpen ? 'btn-secondary' : 'btn-primary'}`}
          onClick={() => setIsFormOpen(!isFormOpen)}
        >
          {isFormOpen ? '✕ Close Form' : '+ Add New Flight'}
        </button>
      </div>

      {/* Add Flight Form */}
      {isFormOpen && (
        <div className="content-card form-card">
          <div className="card-header">
            <h3>Add Flight Schedule</h3>
          </div>
          <form onSubmit={handleFormSubmit} className="styled-form">
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="flightNumber">Flight Number *</label>
                <input
                  id="flightNumber"
                  name="flightNumber"
                  type="text"
                  placeholder="e.g. AI-101, 6E-204"
                  value={formData.flightNumber}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="sourceAirport">Source Airport Code *</label>
                <input
                  id="sourceAirport"
                  name="sourceAirport"
                  type="text"
                  maxLength="5"
                  placeholder="e.g. BOM (Mumbai)"
                  value={formData.sourceAirport}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="destinationAirport">Destination Airport Code *</label>
                <input
                  id="destinationAirport"
                  name="destinationAirport"
                  type="text"
                  maxLength="5"
                  placeholder="e.g. DEL (Delhi)"
                  value={formData.destinationAirport}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="departureTime">Departure Date & Time *</label>
                <input
                  id="departureTime"
                  name="departureTime"
                  type="datetime-local"
                  value={formData.departureTime}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="arrivalTime">Arrival Date & Time *</label>
                <input
                  id="arrivalTime"
                  name="arrivalTime"
                  type="datetime-local"
                  value={formData.arrivalTime}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="flightStatus">Flight Status</label>
                <select
                  id="flightStatus"
                  name="status"
                  value={formData.status}
                  onChange={handleInputChange}
                >
                  <option value="Scheduled">Scheduled</option>
                  <option value="Delayed">Delayed</option>
                  <option value="Cancelled">Cancelled</option>
                </select>
              </div>
            </div>

            <div className="form-actions">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setIsFormOpen(false)}
                disabled={submitting}
              >
                Cancel
              </button>
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? 'Saving Flight...' : 'Save Flight Schedule'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Flights Table */}
      <div className="content-card">
        <div className="card-header">
          <div className="card-header-title">
            <h3>Scheduled Flight Operations</h3>
            <span className="record-count">{flights.length} flights</span>
          </div>
          <button className="btn btn-sm btn-outline" onClick={fetchFlights}>
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading flight schedules...</p>
          </div>
        ) : flights.length === 0 ? (
          <div className="empty-state">
            <p>No flights scheduled.</p>
            <button className="btn btn-primary" onClick={() => setIsFormOpen(true)}>Add Flight</button>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Flight No.</th>
                  <th>Route</th>
                  <th>Departure Time</th>
                  <th>Arrival Time</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {flights.map((f) => (
                  <tr key={f.flightId}>
                    <td>#{f.flightId}</td>
                    <td><strong className="flight-code">{f.flightNumber}</strong></td>
                    <td>
                      <span className="route-badge">
                        {f.sourceAirport} ➔ {f.destinationAirport}
                      </span>
                    </td>
                    <td>{formatDateTime(f.departureTime)}</td>
                    <td>{formatDateTime(f.arrivalTime)}</td>
                    <td>{getStatusBadge(f.status)}</td>
                    <td>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => setDeleteTarget(f)}
                        title="Delete Flight"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      <ConfirmModal
        isOpen={!!deleteTarget}
        title="Confirm Flight Deletion"
        message={`Are you sure you want to delete flight "${deleteTarget?.flightNumber}" (${deleteTarget?.sourceAirport} ➔ ${deleteTarget?.destinationAirport})? Note: If active reservations exist, MySQL relational constraints will reject deletion.`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmText="Yes, Delete Flight"
        isDanger={true}
      />
    </div>
  );
}
