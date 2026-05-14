import React, { useState } from 'react';

export default function BookingForm({ flightNo, onBookingComplete }) {
  const [sectionIndex, setSectionIndex] = useState(1); // 1-based section targeting
  const [passengers, setPassengers] = useState([
    { firstName: '', lastName: '', dob: '1990/01/01', preference: 'X' }
  ]);
  const [status, setStatus] = useState({ message: '', isError: false });

  // Update specific fields within the dynamic passenger registration list
  const updatePassengerField = (index, field, value) => {
    const updated = [...passengers];
    updated[index][field] = value;
    setPassengers(updated);
  };

  const appendPassengerSlot = () => {
    setPassengers([...passengers, { firstName: '', lastName: '', dob: '1990/01/01', preference: 'X' }]);
  };

  const removePassengerSlot = (index) => {
    if (passengers.length > 1) {
      setPassengers(passengers.filter((_, i) => i !== index));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setStatus({ message: 'Processing layout assignments...', isError: false });

    // Validate standard fields securely before triggering external HTTP connections
    const cleanPassengers = passengers.map(p => ({
      firstName: p.firstName.trim() || 'Unknown',
      lastName: p.lastName.trim() || 'Passenger',
      dob: p.dob || '1990/01/01',
      preference: p.preference
    }));

    const payload = {
      sectionIndex: parseInt(sectionIndex, 10),
      passengers: cleanPassengers
    };

    try {
      const response = await fetch(`http://localhost:8080/api/flights/${flightNo}/reserve`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const responseText = await response.text();

      if (response.ok) {
        setStatus({ message: '✓ Booking sequence executed successfully!', isError: false });
        // Reset dynamic tracking forms cleanly for subsequent booking cycles
        setPassengers([{ firstName: '', lastName: '', dob: '1990/01/01', preference: 'X' }]);
        onBookingComplete(); // Trigger root dashboard layout refreshes natively
      } else {
        setStatus({ message: `✕ ${responseText}`, isError: true });
      }
    } catch (error) {
      setStatus({ message: '✕ Network transport fault reaching API backend.', isError: true });
    }
  };

  return (
    <div className="input-desk">
      <div className="desk-header">
        <h3>Automated Booking Counter</h3>
        <p>// Target placements enforce algorithmic neighbor clustering preferences</p>
      </div>

      <div className="desk-body">
        <form onSubmit={handleSubmit}>
          <div className="field-group">
            <label>Target Section Layout</label>
            <select value={sectionIndex} onChange={(e) => setSectionIndex(e.target.value)}>
              <option value={1}>Section 1 — Primary Base Layout</option>
              <option value={2}>Section 2 — Secondary Tier Layout</option>
              <option value={3}>Section 3 — Extended Tier Layout</option>
            </select>
          </div>

          <div className="roster-header">
            <span className="roster-title">Passenger Roster</span>
            <span className="roster-count">{passengers.length} PAX</span>
          </div>

          {passengers.map((p, idx) => (
            <div key={idx} className="passenger-entry-card">
              <div className="pax-card-header">
                <span className="pax-slot-label">Slot #{String(idx + 1).padStart(2, '0')}</span>
                {passengers.length > 1 && (
                  <button type="button" className="pax-remove-btn" onClick={() => removePassengerSlot(idx)}>
                    ✕ Remove
                  </button>
                )}
              </div>

              <div className="pax-name-row">
                <input placeholder="First Name" value={p.firstName} onChange={(e) => updatePassengerField(idx, 'firstName', e.target.value)} required />
                <input placeholder="Last Name" value={p.lastName} onChange={(e) => updatePassengerField(idx, 'lastName', e.target.value)} required />
              </div>

              <div className="pax-detail-row">
                <input type="text" placeholder="YYYY/MM/DD" value={p.dob} onChange={(e) => updatePassengerField(idx, 'dob', e.target.value)} />
                <select value={p.preference} onChange={(e) => updatePassengerField(idx, 'preference', e.target.value)}>
                  <option value="X">No Pref</option>
                  <option value="W">Window</option>
                  <option value="A">Aisle</option>
                </select>
              </div>
            </div>
          ))}

          <button type="button" className="btn-secondary" onClick={appendPassengerSlot}>
            + Add Passenger Slot
          </button>

          <button type="submit" className="btn-primary">
            Commit System Reservation
          </button>
        </form>

        {status.message && (
          <div className={`status-msg ${status.isError ? 'error' : 'success'}`}>
            {status.message}
          </div>
        )}
      </div>
    </div>
  );
}