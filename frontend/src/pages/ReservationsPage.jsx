import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';

export default function ReservationsPage({ setNotification }) {
  const [reservations, setReservations] = useState([]);
  const [passengers, setPassengers] = useState([]);
  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    passengerId: '',
    flightId: '',
    seatNumber: ''
  });

  // Modal delete target
  const [deleteTarget, setDeleteTarget] = useState(null);

  const loadData = async () => {
    try {
      setLoading(true);
      const [resData, passData, flightData] = await Promise.all([
        api.getReservations(),
        api.getPassengers(),
        api.getFlights()
      ]);
      setReservations(resData);
      setPassengers(passData);
      setFlights(flightData);
    } catch (err) {
      setNotification({ message: 'Failed to load reservations data: ' + err.message, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    if (!formData.passengerId) {
      setNotification({ message: 'Please select an existing passenger from the dropdown.', type: 'error' });
      return;
    }
    if (!formData.flightId) {
      setNotification({ message: 'Please select a scheduled flight from the dropdown.', type: 'error' });
      return;
    }
    if (!formData.seatNumber.trim()) {
      setNotification({ message: 'Please enter a seat number (e.g. 12A).', type: 'error' });
      return;
    }

    try {
      setSubmitting(true);
      const payload = {
        passengerId: parseInt(formData.passengerId, 10),
        flightId: parseInt(formData.flightId, 10),
        seatNumber: formData.seatNumber.trim().toUpperCase()
      };

      await api.createReservation(payload);
      setNotification({
        message: `Reservation confirmed for Seat ${payload.seatNumber}!`,
        type: 'success'
      });

      // Reset form
      setFormData({
        passengerId: '',
        flightId: '',
        seatNumber: ''
      });
      setIsFormOpen(false);

      // Refresh table
      const updated = await api.getReservations();
      setReservations(updated);
    } catch (err) {
      setNotification({ message: err.message || 'Error booking reservation.', type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteReservation(deleteTarget.reservationId);
      setNotification({
        message: `Reservation #${deleteTarget.reservationId} (Seat ${deleteTarget.seatNumber}) cancelled.`,
        type: 'success'
      });
      setDeleteTarget(null);
      const updated = await api.getReservations();
      setReservations(updated);
    } catch (err) {
      setNotification({ message: err.message || 'Error cancelling reservation.', type: 'error' });
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

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h2>Reservation Management</h2>
          <p className="page-subtitle">Book passenger seats, track reservations, and enforce duplicate seat prevention</p>
        </div>
        <button
          className={`btn ${isFormOpen ? 'btn-secondary' : 'btn-primary'}`}
          onClick={() => setIsFormOpen(!isFormOpen)}
        >
          {isFormOpen ? '✕ Close Form' : '+ New Reservation'}
        </button>
      </div>

      {/* Create Reservation Form */}
      {isFormOpen && (
        <div className="content-card form-card">
          <div className="card-header">
            <h3>Book Flight Seat for Passenger</h3>
          </div>
          <form onSubmit={handleFormSubmit} className="styled-form">
            <div className="form-grid">
              {/* Passenger Dropdown */}
              <div className="form-group">
                <label htmlFor="passengerSelect">Select Passenger *</label>
                <select
                  id="passengerSelect"
                  name="passengerId"
                  value={formData.passengerId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">-- Choose Registered Passenger --</option>
                  {passengers.map((p) => (
                    <option key={p.passengerId} value={p.passengerId}>
                      {p.fullName} (Passport: {p.passportNumber})
                    </option>
                  ))}
                </select>
                {passengers.length === 0 && (
                  <span className="field-hint error-hint">No passengers found. Please register a passenger first.</span>
                )}
              </div>

              {/* Flight Dropdown */}
              <div className="form-group">
                <label htmlFor="flightSelect">Select Flight *</label>
                <select
                  id="flightSelect"
                  name="flightId"
                  value={formData.flightId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">-- Choose Scheduled Flight --</option>
                  {flights.map((f) => (
                    <option key={f.flightId} value={f.flightId}>
                      {f.flightNumber} ({f.sourceAirport} ➔ {f.destinationAirport}) - {f.status}
                    </option>
                  ))}
                </select>
                {flights.length === 0 && (
                  <span className="field-hint error-hint">No flights found. Please add a flight schedule first.</span>
                )}
              </div>

              {/* Seat Number */}
              <div className="form-group">
                <label htmlFor="seatNumber">Seat Number * (e.g. 12A, 04C)</label>
                <input
                  id="seatNumber"
                  name="seatNumber"
                  type="text"
                  placeholder="e.g. 12A"
                  value={formData.seatNumber}
                  onChange={handleInputChange}
                  maxLength="5"
                  required
                />
                <span className="field-hint">Seat cannot be booked twice on the same flight.</span>
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
              <button
                type="submit"
                className="btn btn-primary"
                disabled={submitting || passengers.length === 0 || flights.length === 0}
              >
                {submitting ? 'Confirming Booking...' : 'Confirm Reservation'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Reservations Table */}
      <div className="content-card">
        <div className="card-header">
          <div className="card-header-title">
            <h3>Active Flight Reservations</h3>
            <span className="record-count">{reservations.length} bookings</span>
          </div>
          <button className="btn btn-sm btn-outline" onClick={loadData}>
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading reservations from MySQL...</p>
          </div>
        ) : reservations.length === 0 ? (
          <div className="empty-state">
            <p>No reservations found in the database.</p>
            <button className="btn btn-primary" onClick={() => setIsFormOpen(true)}>Create First Reservation</button>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Booking ID</th>
                  <th>Passenger Name</th>
                  <th>Flight Number</th>
                  <th>Route</th>
                  <th>Seat Number</th>
                  <th>Booking Date</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map((r) => (
                  <tr key={r.reservationId}>
                    <td>#{r.reservationId}</td>
                    <td>
                      <div className="passenger-cell">
                        <strong>{r.passengerName}</strong>
                        <span className="sub-text">{r.passengerEmail}</span>
                      </div>
                    </td>
                    <td><strong className="flight-code">{r.flightNumber}</strong></td>
                    <td><span className="route-badge">{r.route}</span></td>
                    <td><span className="seat-badge">{r.seatNumber}</span></td>
                    <td>{formatDateTime(r.bookingDate)}</td>
                    <td>
                      <span className={`badge ${r.status === 'Confirmed' ? 'badge-success' : 'badge-danger'}`}>
                        {r.status}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => setDeleteTarget(r)}
                        title="Cancel Reservation"
                      >
                        Cancel
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
        title="Cancel Reservation"
        message={`Are you sure you want to cancel Reservation #${deleteTarget?.reservationId} for ${deleteTarget?.passengerName} (Flight: ${deleteTarget?.flightNumber}, Seat: ${deleteTarget?.seatNumber})?`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmText="Yes, Cancel Booking"
        isDanger={true}
      />
    </div>
  );
}
