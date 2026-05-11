package com.airline.system.service;

import com.airline.system.model.AirlineSystem;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Service
public class AirlineService {
    
    private final AirlineSystem airlineSystem;

    public AirlineService() {
        this.airlineSystem = new AirlineSystem();
    }

    @PostConstruct
    public void init() {
        try {
            airlineSystem.loadFlights("src/main/resources/flights.txt");
            System.out.println("Flights loaded successfully into persistent memory.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AirlineSystem getSystem() {
        return airlineSystem;
    }
}