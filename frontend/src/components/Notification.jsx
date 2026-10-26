import React from 'react';

export default function Notification({ message, type, onClose }) {
  if (!message) return null;

  const isSuccess = type === 'success';

  return (
    <div className={`notification-banner ${isSuccess ? 'notification-success' : 'notification-error'}`}>
      <div className="notification-content">
        <span className="notification-icon">{isSuccess ? '✅' : '⚠️'}</span>
        <span className="notification-text">{message}</span>
      </div>
      <button type="button" className="notification-close" onClick={onClose} aria-label="Close notification">
        &times;
      </button>
    </div>
  );
}
