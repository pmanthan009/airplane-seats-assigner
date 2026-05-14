import React from 'react';

export default function FlightGrid({ flightData }) {
  if (!flightData || !flightData.sections) {
    return (
      <div className="grid-container" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '300px' }}>
        <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--text-dim)', letterSpacing: '0.1em' }}>
          // Select a flight tab above to generate active map structures.
        </span>
      </div>
    );
  }

  return (
    <div className="grid-container">
      <div className="grid-header">
        <h2>Operational Map — Flight {flightData.flightNo}</h2>
        <span className="grid-date-badge">{flightData.date || 'Active'}</span>
      </div>

      <div className="grid-body">
        {flightData.sections.map((sec, secIdx) => {
          // Safely extract core layout properties exposed directly by Jackson
          const baseRow = sec.baseRowNumber || 1;
          const totalRows = sec.rows || 0;
          const blocksConfig = sec.blocks || [2];
          const seatMatrix = sec.seats || [];

          return (
            <div key={secIdx} className="section-wrapper">
              <div className="section-title-row">
                <span className="section-label">Section #{secIdx + 1}</span>
                <span className="section-meta-tag">
                  Blocks: {blocksConfig.join(' × ')}
                </span>
              </div>

              {/* Loop cleanly across the vertical array structures */}
              {Array.from({ length: totalRows }).map((_, rowLocalIdx) => {
                const absRow = baseRow + rowLocalIdx;
                const rowSeatsArray = seatMatrix[rowLocalIdx] || [];
                let flatSeatCounter = 0; // Tracks consecutive column indices mapping character sets cleanly

                return (
                  <div key={absRow} className="row-Display">
                    <div className="row-tag">Row {absRow}</div>

                    <div className="seats-row">
                      {/* Render visual column groupings separated naturally by aisle margins */}
                      {blocksConfig.map((blockSize, blockIdx) => (
                        <div key={blockIdx} className="block-group">
                          {Array.from({ length: blockSize }).map(() => {
                            const currentSeat = rowSeatsArray[flatSeatCounter];
                            const seatChar = String.fromCharCode(65 + flatSeatCounter);
                            flatSeatCounter++;

                            const occupant = currentSeat ? currentSeat.occupant : null;
                            const isBooked = occupant && occupant.lastName;
                            
                            // Formats view explicitly as "J. Doe" if allocated, else native indexing tags
                            const displayLabel = isBooked 
                              ? `${occupant.firstName.charAt(0)}. ${occupant.lastName}`
                              : seatChar;

                            return (
                              <div 
                                key={seatChar}
                                className={`seat-box ${isBooked ? 'booked' : 'avail'}`}
                                title={isBooked ? `ID: ${occupant.passengerID} | DOB: ${occupant.dob} | Pref: ${occupant.preference}` : 'Seat Available'}
                              >
                                {displayLabel}
                              </div>
                            );
                          })}
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          );
        })}
      </div>

      <div className="grid-legend">
        <div className="legend-item">
          <span className="legend-dot avail" />
          Available
        </div>
        <div className="legend-item">
          <span className="legend-dot booked" />
          Occupied
        </div>
      </div>
    </div>
  );
}