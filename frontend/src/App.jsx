import React, { useState, useEffect } from 'react';
import BookingForm from './components/BookingForm';
import FlightGrid from './components/FlightGrid';
import './App.css';

export default function App() {
  const [fleetInventory, setFleetInventory] = useState([]);
  const [selectedFlightNo, setSelectedFlightNo] = useState(null);
  const [activeFlightLayout, setActiveFlightLayout] = useState(null);
  const [status, setStatus] = useState({ isLoading: true, error: null });

  // 1. Initial execution trigger polling high-level inventory profiles
  useEffect(() => {
    fetchSystemFleet();
  }, []);

  // 2. Reactive state hook querying detailed maps whenever navigation targets switch
  useEffect(() => {
    if (selectedFlightNo !== null) {
      fetchDetailedFlightMap(selectedFlightNo);
    }
  }, [selectedFlightNo]);

  const fetchSystemFleet = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/flights');
      if (!res.ok) throw new Error('Transport protocol exception parsing base fleet summaries.');
      const data = await res.json();
      
      setFleetInventory(data);
      setStatus({ isLoading: false, error: null });

      // Focus tab selection automatically targeting initial configurations mapped if unassigned
      if (data.length > 0 && selectedFlightNo === null) {
        // Check if backend exposes direct nested objects or flat summaries cleanly
        const initialId = data[0].flightNo || data[0]; 
        setSelectedFlightNo(initialId);
      }
    } catch (err) {
      setStatus({ isLoading: false, error: 'Critical network error fetching initial fleet list.' });
    }
  };

  const fetchDetailedFlightMap = async (targetId) => {
    try {
      const res = await fetch(`http://localhost:8080/api/flights/${targetId}`);
      if (!res.ok) throw new Error('Exception reading layout records.');
      const mapData = await res.json();
      setActiveFlightLayout(mapData);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="dashboard-root">
      <header className="app-header">
        <div>
          <h1>Airline Operations Command</h1>
          <span className="subtitle">// Stateless API Desk Interface</span>
        </div>
        
        <div className="status-badge">
          <span className={`status-dot ${status.error ? 'offline' : 'online'}`} />
          <span className="status-label">
            {status.error ? 'Offline' : 'API Connection Verified'}
          </span>
        </div>
      </header>

      {/* Navigation Layer */}
      <div className="nav-section">
        <span className="nav-section-label">// Select Active Target Flight</span>
        <div className="nav-tabs">
          {fleetInventory.map((item, idx) => {
            const fltNo = item.flightNo || item;
            const labelStr = item.date ? `Flight ${fltNo} (${item.date})` : `Flight ${fltNo}`;
            return (
              <button 
                key={idx} 
                className={`tab-button ${selectedFlightNo === fltNo ? 'active' : ''}`}
                onClick={() => setSelectedFlightNo(fltNo)}
              >
                {labelStr}
              </button>
            );
          })}
        </div>
      </div>

      {/* Primary Layout Container */}
      <main className="content-grid">
        {selectedFlightNo ? (
          <>
            <BookingForm 
              flightNo={selectedFlightNo} 
              onBookingComplete={() => fetchDetailedFlightMap(selectedFlightNo)} 
            />
            <FlightGrid 
              flightData={activeFlightLayout} 
            />
          </>
        ) : (
          <div className="empty-state">
            {status.isLoading ? '// Retrieving aircraft configuration layouts...' : '// No active layouts selected.'}
          </div>
        )}
      </main>
    </div>
  );
}