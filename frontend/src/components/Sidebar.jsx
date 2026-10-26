import React from 'react';

export default function Sidebar({ activePage, setActivePage }) {
  const menuItems = [
    { id: 'dashboard', label: 'Dashboard', icon: '📊' },
    { id: 'employees', label: 'Employees', icon: '👨‍✈️' },
    { id: 'passengers', label: 'Passengers', icon: '👥' },
    { id: 'flights', label: 'Flights & Routes', icon: '✈️' },
    { id: 'reservations', label: 'Reservations', icon: '🎫' },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="brand-icon">🛫</span>
        <div className="brand-text">
          <h2>SkyAdmin</h2>
          <span>Airline Management</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <ul>
          {menuItems.map((item) => (
            <li key={item.id}>
              <button
                type="button"
                className={`nav-button ${activePage === item.id ? 'active' : ''}`}
                onClick={() => setActivePage(item.id)}
              >
                <span className="nav-icon">{item.icon}</span>
                <span className="nav-label">{item.label}</span>
              </button>
            </li>
          ))}
        </ul>
      </nav>

      <div className="sidebar-footer">
        <div className="system-pill">
          <span className="dot online"></span>
          <span>MySQL / JDBC Connected</span>
        </div>
        <small className="course-tag">SE Comp A • DBMS Mini-Project</small>
      </div>
    </aside>
  );
}
