package com.airline.system.dto;

public class GroupReservationRequest {
    private int flightNo;
    private int sectionIndex; // 1-based index
    private String[] passengerNames;

    // Getters and Setters required for Spring JSON mapping
    public int getFlightNo() { return flightNo; }
    public void setFlightNo(int flightNo) { this.flightNo = flightNo; }
    public int getSectionIndex() { return sectionIndex; }
    public void setSectionIndex(int sectionIndex) { this.sectionIndex = sectionIndex; }
    public String[] getPassengerNames() { return passengerNames; }
    public void setPassengerNames(String[] passengerNames) { this.passengerNames = passengerNames; }
}