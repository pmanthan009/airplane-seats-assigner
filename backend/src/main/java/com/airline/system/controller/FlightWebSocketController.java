package com.airline.system.controller;

// 1. Spring Framework Imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

// 2. Service and DTO Imports
import com.airline.system.service.AirlineService;
import com.airline.system.dto.GroupReservationRequest;

// 3. Core Domain Model Imports
import com.airline.system.model.Flight;
import com.airline.system.model.Passenger;

@Controller
public class FlightWebSocketController {

    private final AirlineService airlineService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public FlightWebSocketController(AirlineService airlineService, SimpMessagingTemplate messagingTemplate) {
        this.airlineService = airlineService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * CHANNEL 1: Initial Data Request
     * Incoming route: /app/getFlight
     */
    @MessageMapping("/getFlight")
    public void getFlightInitialState(int flightNo) {
        Flight flight = airlineService.getSystem().findFlight(flightNo);
        if (flight != null) {
            messagingTemplate.convertAndSend("/topic/flight/" + flightNo, flight);
        }
    }

    /**
     * CHANNEL 2: Process a Group Automated Reservation
     * Incoming route: /app/reserveGroup
     */
    @MessageMapping("/reserveGroup")
    public void processGroupReservation(GroupReservationRequest request) {
        Flight flight = airlineService.getSystem().findFlight(request.getFlightNo());
        
        if (flight != null && request.getPassengerNames() != null) {
            Passenger[] group = new Passenger[request.getPassengerNames().length];
            for (int i = 0; i < group.length; i++) {
                String fullName = request.getPassengerNames()[i].trim();
                
                String[] nameParts = fullName.split("\\s+");
                String firstName = nameParts.length > 0 ? nameParts[0] : "Unknown";
                String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

                // Populate with: Last Name, First Name, Default DOB, Default Preference
                group[i] = new Passenger(lastName, firstName, "N/A", 'X');
            }
            
            boolean success = flight.reserve(request.getSectionIndex(), group);
            
            if (success) {
                messagingTemplate.convertAndSend("/topic/flight/" + request.getFlightNo(), flight);
            }
        }
    }
}