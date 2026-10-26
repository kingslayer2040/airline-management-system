import React, { useState, useEffect } from 'react';
import { api } from '../services/api';
import ConfirmModal from '../components/ConfirmModal';

export default function EmployeesPage({ setNotification }) {
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    fullName: '',
    specialty: 'Pilot',
    contactNumber: '',
    email: '',
    employmentStatus: 'Active'
  });

  // Modal delete confirmation state
  const [deleteTarget, setDeleteTarget] = useState(null);

  const fetchEmployees = async () => {
    try {
      setLoading(true);
      const data = await api.getEmployees();
      setEmployees(data);
    } catch (err) {
      setNotification({ message: 'Failed to load employees: ' + err.message, type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees();
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();

    // Client-side validations
    if (!formData.fullName.trim()) {
      setNotification({ message: 'Please enter employee full name.', type: 'error' });
      return;
    }
    if (!formData.email.trim() || !formData.email.includes('@')) {
      setNotification({ message: 'Please enter a valid email address.', type: 'error' });
      return;
    }
    if (!formData.contactNumber.trim()) {
      setNotification({ message: 'Please enter a contact number.', type: 'error' });
      return;
    }

    try {
      setSubmitting(true);
      const created = await api.createEmployee(formData);
      setNotification({ message: `Employee "${created.fullName}" successfully registered!`, type: 'success' });
      
      // Reset form & close
      setFormData({
        fullName: '',
        specialty: 'Pilot',
        contactNumber: '',
        email: '',
        employmentStatus: 'Active'
      });
      setIsFormOpen(false);
      
      // Refresh list
      fetchEmployees();
    } catch (err) {
      setNotification({ message: err.message || 'Error creating employee record.', type: 'error' });
    } finally {
      setSubmitting(false);
    }
  };

  const confirmDelete = async () => {
    if (!deleteTarget) return;
    try {
      await api.deleteEmployee(deleteTarget.employeeId);
      setNotification({ message: `Employee "${deleteTarget.fullName}" deleted successfully.`, type: 'success' });
      setDeleteTarget(null);
      fetchEmployees();
    } catch (err) {
      setNotification({ message: err.message || 'Error deleting employee.', type: 'error' });
      setDeleteTarget(null);
    }
  };

  const getSpecialtyBadge = (spec) => {
    if (spec === 'Pilot') return <span className="badge badge-primary">👨‍✈️ Pilot</span>;
    if (spec === 'Cabin Crew') return <span className="badge badge-sky">👔 Cabin Crew</span>;
    if (spec === 'Engineer') return <span className="badge badge-warning">🛠️ Engineer</span>;
    return <span className="badge">{spec}</span>;
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <h2>Employee Management</h2>
          <p className="page-subtitle">Manage airline crew, cockpit crew, and ground engineering staff</p>
        </div>
        <button
          className={`btn ${isFormOpen ? 'btn-secondary' : 'btn-primary'}`}
          onClick={() => setIsFormOpen(!isFormOpen)}
        >
          {isFormOpen ? '✕ Close Form' : '+ Add New Employee'}
        </button>
      </div>

      {/* Add Employee Form Card */}
      {isFormOpen && (
        <div className="content-card form-card">
          <div className="card-header">
            <h3>Register New Airline Employee</h3>
          </div>
          <form onSubmit={handleFormSubmit} className="styled-form">
            <div className="form-grid">
              <div className="form-group">
                <label htmlFor="fullName">Full Name *</label>
                <input
                  id="fullName"
                  name="fullName"
                  type="text"
                  placeholder="e.g. Captain Rajesh Sharma"
                  value={formData.fullName}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="specialty">Specialty *</label>
                <select
                  id="specialty"
                  name="specialty"
                  value={formData.specialty}
                  onChange={handleInputChange}
                  required
                >
                  <option value="Pilot">Pilot</option>
                  <option value="Cabin Crew">Cabin Crew</option>
                  <option value="Engineer">Engineer</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="email">Email Address *</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="e.g. rajesh.sharma@airsky.com"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="contactNumber">Contact Number *</label>
                <input
                  id="contactNumber"
                  name="contactNumber"
                  type="text"
                  placeholder="e.g. +91 98201 12345"
                  value={formData.contactNumber}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="form-group">
                <label htmlFor="employmentStatus">Employment Status</label>
                <select
                  id="employmentStatus"
                  name="employmentStatus"
                  value={formData.employmentStatus}
                  onChange={handleInputChange}
                >
                  <option value="Active">Active</option>
                  <option value="Inactive">Inactive</option>
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
                {submitting ? 'Saving to Database...' : 'Save Employee'}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Employees Table Card */}
      <div className="content-card">
        <div className="card-header">
          <div className="card-header-title">
            <h3>Registered Employees</h3>
            <span className="record-count">{employees.length} records</span>
          </div>
          <button className="btn btn-sm btn-outline" onClick={fetchEmployees}>
            🔄 Refresh
          </button>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading employee records...</p>
          </div>
        ) : employees.length === 0 ? (
          <div className="empty-state">
            <p>No employees found in the database.</p>
            <button className="btn btn-primary" onClick={() => setIsFormOpen(true)}>Add Employee</button>
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Full Name</th>
                  <th>Specialty</th>
                  <th>Email</th>
                  <th>Contact</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {employees.map((emp) => (
                  <tr key={emp.employeeId}>
                    <td>#{emp.employeeId}</td>
                    <td><strong>{emp.fullName}</strong></td>
                    <td>{getSpecialtyBadge(emp.specialty)}</td>
                    <td>{emp.email}</td>
                    <td>{emp.contactNumber}</td>
                    <td>
                      <span className={`badge ${emp.employmentStatus === 'Active' ? 'badge-success' : 'badge-danger'}`}>
                        {emp.employmentStatus}
                      </span>
                    </td>
                    <td>
                      <button
                        className="btn btn-sm btn-danger"
                        onClick={() => setDeleteTarget(emp)}
                        title="Delete Employee"
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

      {/* Confirmation Modal */}
      <ConfirmModal
        isOpen={!!deleteTarget}
        title="Confirm Employee Deletion"
        message={`Are you sure you want to permanently delete employee "${deleteTarget?.fullName}" (ID: #${deleteTarget?.employeeId}) from MySQL?`}
        onConfirm={confirmDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmText="Yes, Delete"
        isDanger={true}
      />
    </div>
  );
}
