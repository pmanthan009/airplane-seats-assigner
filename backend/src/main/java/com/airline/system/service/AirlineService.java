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
            System.out.println("REST Backend Core Initialized: Flights mapped successfully.");
        } catch (IOException e) {
            System.err.println("Critical initialization exception loading base configuration:");
            e.printStackTrace();
        }
    }

    public AirlineSystem getSystem() {
        return airlineSystem;
    }
}