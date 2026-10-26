import React, { useState, useEffect } from 'react';

export default function Header({ pageTitle }) {
  const [currentTime, setCurrentTime] = useState(new Date().toLocaleTimeString());

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date().toLocaleTimeString());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const today = new Date().toLocaleDateString('en-US', {
    weekday: 'short',
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });

  return (
    <header className="top-header">
      <div className="header-left">
        <h1 className="header-title">Airline Management System</h1>
        <span className="page-breadcrumb">/ {pageTitle}</span>
      </div>

      <div className="header-right">
        <div className="header-clock">
          <span className="date-display">{today}</span>
          <span className="time-display">{currentTime}</span>
        </div>

        <div className="user-profile">
          <div className="avatar">A</div>
          <div className="user-info">
            <span className="user-name">Administrator</span>
            <span className="user-role">Operations Control</span>
          </div>
        </div>
      </div>
    </header>
  );
}
