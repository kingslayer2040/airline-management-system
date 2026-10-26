// API Service for interacting with Spring Boot Backend REST Endpoints
const API_BASE = '/api';

async function request(url, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  };

  try {
    const response = await fetch(`${API_BASE}${url}`, config);
    const contentType = response.headers.get('content-type');
    const isJson = contentType && contentType.includes('application/json');
    const data = isJson ? await response.json() : await response.text();

    if (!response.ok) {
      const errorMessage = (data && data.error) ? data.error : (typeof data === 'string' && data ? data : `Error ${response.status}: ${response.statusText}`);
      throw new Error(errorMessage);
    }

    return data;
  } catch (err) {
    console.error(`API Error [${options.method || 'GET'} ${url}]:`, err);
    throw err;
  }
}

export const api = {
  // Dashboard
  getDashboard: () => request('/dashboard'),

  // Employees
  getEmployees: () => request('/employees'),
  createEmployee: (employee) => request('/employees', {
    method: 'POST',
    body: JSON.stringify(employee)
  }),
  deleteEmployee: (id) => request(`/employees/${id}`, {
    method: 'DELETE'
  }),

  // Passengers
  getPassengers: () => request('/passengers'),
  createPassenger: (passenger) => request('/passengers', {
    method: 'POST',
    body: JSON.stringify(passenger)
  }),
  deletePassenger: (id) => request(`/passengers/${id}`, {
    method: 'DELETE'
  }),

  // Flights
  getFlights: () => request('/flights'),
  createFlight: (flight) => request('/flights', {
    method: 'POST',
    body: JSON.stringify(flight)
  }),
  deleteFlight: (id) => request(`/flights/${id}`, {
    method: 'DELETE'
  }),

  // Reservations
  getReservations: () => request('/reservations'),
  createReservation: (reservation) => request('/reservations', {
    method: 'POST',
    body: JSON.stringify(reservation)
  }),
  deleteReservation: (id) => request(`/reservations/${id}`, {
    method: 'DELETE'
  })
};
