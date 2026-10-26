import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';

export default function PassengersPage({ setNotification }) {
  const [passengers, setPassengers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    contactNumber: '',
    passportNumber: ''
  });

  // Modal delete target
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchPassengers = async () => {
    try {
      setLoading(true);
      const data = await api.getPassengers();
      setPassengers(data);
    } catch (err) {
      setNotification({ message: 'Failed to load passengers: ' + err.message, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPassengers();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    // Client-side validations
    if (!formData.fullName.trim()) {
      setNotification({ message: 'Passenger full name is required.', type: 'error' });
      return;
    }
    if (!formData.email.trim() || !formData.email.includes('@')) {
      setNotification({ message: 'A valid passenger email is required.', type: 'error' });
      return;
    }
    if (!formData.contactNumber.trim()) {
      setNotification({ message: 'Contact number is required.', type: 'error' });
      return;
    }
    if (!formData.passportNumber.trim()) {
      setNotification({ message: 'Passport number is required.', type: 'error' });
      return;
    }

    try {
      setSubmitting(true);
      const created = await api.createPassenger(formData);
      setNotification({ message: `Passenger "${created.fullName}" (Passport: ${created.passportNumber}) registered successfully!`, type: 'success' });

      // Reset form
      setFormData({
        fullName: '',
        email: '',
        contactNumber: '',
        passportNumber: ''
      });
      setIsFormOpen(false);

      fetchPassengers();
    } catch (err) {
      setNotification({ message: err.message || 'Error registering passenger.', type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deletePassenger(deleteTarget.passengerId);
      setNotification({ message: `Passenger "${deleteTarget.fullName}" deleted successfully.`, type: 'success' });
      setDeleteTarget(null);
      fetchPassengers();
    } catch (err) {
      setNotification({ message: err.message || 'Error deleting passenger.', type: 'error' });
      setDeleteTarget(null);
    }
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h2>Passenger Management</h2>
          <p className="page-subtitle">Register passengers, maintain contact records, and verify passport integrity</p>
        </div>
        <button
          className={`btn ${isFormOpen ? 'btn-secondary' : 'btn-primary'}`}
          onClick={() => setIsFormOpen(!isFormOpen)}
        >
          {isFormOpen ? '✕ Close Form' : '+ Register Passenger'}
        </button>
      </div>

      {/* Add Passenger Form */}
      {isFormOpen && (
        <div className="content-card form-card">
          <div className="card-header">
            <h3>Register New Passenger</h3>
          </div>
          <form onSubmit={handleFormSubmit} className="styled-form">
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="pFullName">Full Name *</label>
                <input
                  id="pFullName"
                  name="fullName"
                  type="text"
                  placeholder="e.g. Aarav Patel"
                  value={formData.fullName}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="pEmail">Email Address *</label>
                <input
                  id="pEmail"
                  name="email"
                  type="email"
                  placeholder="e.g. aarav.patel@gmail.com"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="pContact">Contact Number *</label>
                <input
                  id="pContact"
                  name="contactNumber"
                  type="text"
                  placeholder="e.g. +91 99111 22334"
                  value={formData.contactNumber}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="pPassport">Passport Number * (Unique)</label>
                <input
                  id="pPassport"
                  name="passportNumber"
                  type="text"
                  placeholder="e.g. Z1234567"
                  value={formData.passportNumber}
                  onChange={handleInputChange}
                  required
                />
                <span className="field-hint">Must be unique across all passengers.</span>
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
                {submitting ? 'Registering...' : 'Register Passenger'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Passengers Table */}
      <div className="content-card">
        <div className="card-header">
          <div className="card-header-title">
            <h3>Registered Passengers</h3>
            <span className="record-count">{passengers.length} passengers</span>
          </div>
          <button className="btn btn-sm btn-outline" onClick={fetchPassengers}>
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading passenger directory...</p>
          </div>
        ) : passengers.length === 0 ? (
          <div className="empty-state">
            <p>No passengers registered yet.</p>
            <button className="btn btn-primary" onClick={() => setIsFormOpen(true)}>Register Passenger</button>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Passenger ID</th>
                  <th>Full Name</th>
                  <th>Passport No.</th>
                  <th>Email</th>
                  <th>Contact Number</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {passengers.map((p) => (
                  <tr key={p.passengerId}>
                    <td>#{p.passengerId}</td>
                    <td><strong>{p.fullName}</strong></td>
                    <td><span className="badge badge-dark">{p.passportNumber}</span></td>
                    <td>{p.email}</td>
                    <td>{p.contactNumber}</td>
                    <td>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => setDeleteTarget(p)}
                        title="Delete Passenger"
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
        title="Confirm Passenger Deletion"
        message={`Are you sure you want to delete passenger "${deleteTarget?.fullName}" (Passport: ${deleteTarget?.passportNumber})? Note: If they have active reservations, MySQL will prevent deletion to preserve relational integrity.`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmText="Yes, Delete Passenger"
        isDanger={true}
      />
    </div>
  );
}
