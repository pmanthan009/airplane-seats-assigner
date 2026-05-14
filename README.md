# Airline Seating Tool (Full Stack)

A stateless, highly optimized Spring Boot RESTful backend designed to automate and manage real-time airline seat allocations according to preferences, continuous group placements, and manifest tracking.

<img width="2880" height="2880" alt="20260514_030733" src="https://github.com/user-attachments/assets/741185ff-26bb-4297-83ec-d29d2cf3b08e" />

---

## Architecture Overview

The application follows a classic **Layered REST Architecture** designed to securely expose rich, underlying custom domain models without sacrificing object encapsulation or performance.

```text
┌────────────────────────────────────────────────────────┐
│                   HTTP / REST Layer                    │
│   (FlightRestController mapped with typed Input DTOs)  │
└───────────────────────────┬────────────────────────────┘
                            │ Delegates JSON payloads
                            ▼
┌────────────────────────────────────────────────────────┐
│                    Service Layer                       │
│    (AirlineService singleton persistent data hub)      │
└───────────────────────────┬────────────────────────────┘
                            │ Invokes core engine logic
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Core Domain Model                    │
│    (AirlineSystem ──► Flight ──► Section ──► Seat)     │
└───────────────────────────┬────────────────────────────┘
                            │ Executes polymorphic strategies
                            ▼
┌────────────────────────────────────────────────────────┐
│             Algorithmic Placement Engine               │
│  (SeatPlacementFinder Strategy implementations & RNG)  │
└────────────────────────────────────────────────────────┘
```
1. **REST Controller Layer (FlightRestController)**: Intercepts external HTTP requests, deserializes payloads into strict Input Data Transfer Objects (DTOs) to guarantee type safety, and translates zero-based API indices natively to align with underlying domain rules.

2. **Service Hub (AirlineService)**: Operates as a stateless singleton wrapper managing system initialization lifecycle hooks (@PostConstruct). It loads offline definitions directly into active memory upon server ignition.

3. **Core Domain Engine**: Features optimized, decoupled business logic utilizing deep custom collections (FlightOrderedList, PassengerOrderedList) to guarantee stable node traversal and linear manifest management.

4. **Polymorphic Placement Engine**: Resolves incoming reservation parameters dynamically using abstract strategy patterns (SeatPlacementFinder) prioritizing strict contiguous grouping preferences natively.

## Tech Stack
Core Runtime: *Java 17 LTS*

Framework: *Spring Boot 3.2.x*

Web Layer: *Spring Web (Embedded Apache Tomcat, Standard REST annotations)*

Serialization: *FasterXML Jackson (Custom auto-detection and deep model isolation rules)*

Build Tool: *Apache Maven*

## API Endpoints Reference

### 1. Fetch Fleet Overview

Retrieves high-level summary structures tracking available base flights.

Method: GET

Route: /api/flights

Response: 200 OK

```JSON
[
  { "flightNo": 1, "date": "2012/02/22" },
  { "flightNo": 222, "date": "2012/02/28" }
]
```
### 2. Poll Specific Structural Layouts

Inspects detailed cross-sectional matrices, blocks configurations, and live passenger occupancy references.

Method: GET

Route: /api/flights/{flightNo}

Response: 200 OK (Returns complete serialized Flight object mapping nested Section configurations).

### 3. Commit Group Reservation Sequence
Processes automated algorithmic positioning logic targeting specific section indices.

Method: POST

Route: /api/flights/{flightNo}/reserve

Headers: Content-Type: application/json

Payload Structure:

```JSON
{
  "sectionIndex": 0, 
  "passengers": [
    { "firstName": "Jane", "lastName": "Doe", "dob": "1992/08/12", "preference": "W" },
    { "firstName": "John", "lastName": "Doe", "dob": "1988/11/04", "preference": "A" }
  ]
}
```

Responses:

200 OK: Party sequence validated and mapped successfully.

400 Bad Request: Faulty input strings or malformed body parameters.

409 Conflict: Insufficient contiguous structural seating capacity available.

## Local Development Setup

### Prerequisites

1. JDK 17+ installed globally and added to system path environment parameters.

2. Apache Maven configured successfully.

### Installation & Launch Steps

1. Verify Offline Data Placement: Ensure your base operational config inputs (flights.txt) reside exactly at the backend/src/main/resources.

2. Execute Engine Compilation:
Open standard administrative command prompt instances targeted inside the backend directory:

```Bash
mvn clean install
mvn spring-boot:run
```

3. Verify Server Binding: The underlying instance auto-initializes embedded runtime ports native to http://localhost:8080. Polling routes return standard cross-origin enabled mapping structures instantly.

## Next Steps

* [ ] Add funtionality to upload flight data (flight.txt) and load flights.
* [ ] Support uploading passenger data file (passengers.txt) to auto-assign all the passengers to appropriate flights.
