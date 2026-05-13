package com.airline.system.controller;

import com.airline.system.service.AirlineService;
import com.airline.system.model.Flight;
import com.airline.system.model.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightRestController {

    private final AirlineService airlineService;

    @Autowired
    public FlightRestController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    // --- Data Transfer Objects (DTOs) for strict request typing ---

    public static class PassengerRequestDTO {
        private String firstName;
        private String lastName;
        private String dob; // Expected format: YYYY/MM/DD
        private char preference; // 'W' for Window, 'A' for Aisle, 'X' for None

        public String getFirstName() { return firstName != null ? firstName.trim() : "Unknown"; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName != null ? lastName.trim() : ""; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getDob() { return dob != null ? dob.trim() : "1990/01/01"; }
        public void setDob(String dob) { this.dob = dob; }
        
        public char getPreference() { return preference != '\0' ? preference : 'X'; }
        public void setPreference(char preference) { this.preference = preference; }
    }

    public static class GroupBookingPayload {
        private int sectionIndex; // Target specific layout section index (0-based or 1-based logic)
        private List<PassengerRequestDTO> passengers;

        public int getSectionIndex() { return sectionIndex; }
        public void setSectionIndex(int sectionIndex) { this.sectionIndex = sectionIndex; }
        
        public List<PassengerRequestDTO> getPassengers() { return passengers; }
        public void setPassengers(List<PassengerRequestDTO> passengers) { this.passengers = passengers; }
    }


    // --- RESTful Endpoints ---

    /**
     * GET /api/flights
     * Returns an inventory array containing all available flight profiles.
     */
    @GetMapping
    public ResponseEntity<Object[]> getSystemFleet() {
        Object[] fleet = airlineService.getSystem().getAllFlights();
        return ResponseEntity.ok(fleet);
    }

    /**
     * GET /api/flights/{flightNo}
     * Inspects target flight structures, generating complete layout matrix views.
     */
    @GetMapping("/{flightNo}")
    public ResponseEntity<Flight> getFlightLayoutState(@PathVariable int flightNo) {
        Flight flight = airlineService.getSystem().findFlight(flightNo);
        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(flight);
    }

    /**
     * POST /api/flights/{flightNo}/reserve
     * Executes assignment rules across requested payload targets.
     */
    @PostMapping("/{flightNo}/reserve")
    public ResponseEntity<String> executeGroupReservation(
            @PathVariable int flightNo,
            @RequestBody GroupBookingPayload payload) {

        Flight flight = airlineService.getSystem().findFlight(flightNo);
        if (flight == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: Target flight identifier missing from database.");
        }

        if (payload.getPassengers() == null || payload.getPassengers().isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Incoming reservation queue is empty.");
        }

        // Map deserialized JSON DTO inputs exactly into standard core Passenger instances
        Passenger[] bookingGroup = new Passenger[payload.getPassengers().size()];
        for (int i = 0; i < bookingGroup.length; i++) {
            PassengerRequestDTO req = payload.getPassengers().get(i);
            bookingGroup[i] = new Passenger(
                    req.getLastName(),
                    req.getFirstName(),
                    req.getDob(),
                    req.getPreference()
            );
        }

        // Delegate placement validation directly to domain structures
        boolean assignedSuccessfully = flight.reserve(payload.getSectionIndex(), bookingGroup);

        if (assignedSuccessfully) {
            return ResponseEntity.ok("Success: Booking sequence verified and passengers assigned cleanly.");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Placement Failure: Unable to locate sufficient structural seating capacity matching request constraints.");
        }
    }
}