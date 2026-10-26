import React, { useState, useEffect } from 'react';
import Sidebar from './components/Sidebar';
import Header from './components/Header';
import Notification from './components/Notification';
import DashboardPage from './pages/DashboardPage';
import EmployeesPage from './pages/EmployeesPage';
import PassengersPage from './pages/PassengersPage';
import FlightsPage from './pages/FlightsPage';
import ReservationsPage from './pages/ReservationsPage';

export default function App() {
  const [activePage, setActivePage] = useState('dashboard');
  const [notification, setNotification] = useState({ message: '', type: 'success' });

  // Auto-dismiss notification after 5 seconds
  useEffect(() => {
    if (notification.message) {
      const timer = setTimeout(() => {
        setNotification({ message: '', type: 'success' });
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [notification]);

  const clearNotification = () => {
    setNotification({ message: '', type: 'success' });
  };

  const getPageTitle = (page) => {
    switch (page) {
      case 'dashboard':
        return 'Dashboard Overview';
      case 'employees':
        return 'Employee Management';
      case 'passengers':
        return 'Passenger Management';
      case 'flights':
        return 'Flight & Route Management';
      case 'reservations':
        return 'Reservation Management';
      default:
        return 'Admin Portal';
    }
  };

  return (
    <div className="app-container">
      {/* Sidebar Navigation */}
      <Sidebar activePage={activePage} setActivePage={setActivePage} />

      {/* Main Content Area */}
      <div className="main-wrapper">
        <Header pageTitle={getPageTitle(activePage)} />

        <main className="main-content">
          {notification.message && (
            <Notification
              message={notification.message}
              type={notification.type}
              onClose={clearNotification}
            />
          )}

          {/* Conditional rendering for pages */}
          {activePage === 'dashboard' && (
            <DashboardPage
              setActivePage={setActivePage}
              setNotification={setNotification}
            />
          )}
          {activePage === 'employees' && (
            <EmployeesPage setNotification={setNotification} />
          )}
          {activePage === 'passengers' && (
            <PassengersPage setNotification={setNotification} />
          )}
          {activePage === 'flights' && (
            <FlightsPage setNotification={setNotification} />
          )}
          {activePage === 'reservations' && (
            <ReservationsPage setNotification={setNotification} />
          )}
        </main>
      </div>
    </div>
  );
}
