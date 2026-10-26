import React from 'react';

export default function StatCard({ title, value, icon, color = 'blue', subtitle }) {
  return (
    <div className={`stat-card stat-card-${color}`}>
      <div className="stat-card-header">
        <span className="stat-card-title">{title}</span>
        <span className="stat-card-icon">{icon}</span>
      </div>
      <div className="stat-card-body">
        <span className="stat-card-value">{value}</span>
      </div>
      {subtitle && (
        <div className="stat-card-footer">
          <span className="stat-card-subtitle">{subtitle}</span>
        </div>
      )}
    </div>
  );
}
