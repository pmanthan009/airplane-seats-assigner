/**
 * Flight
 * 
 * PURPOSE: Represents a flight: its date, sections, passenger manifest, and seat-assignment logic.
 */

package com.airline.system.model;
import java.util.Random;

public class Flight extends OrderedData {
    private int flightNo;
    private String date;
    private Section[] sections;
    private PassengerOrderedList manifest;
    private int nextPid = 100;

    private SeatPlacementFinder[] finders;
    private static final Random RNG = new Random();

    /**
     * Flight constructor
     * param: flightNo (int), date (String), sections (Section[])
     * initializes a Flight with its number, date, seating sections and manifest
     * return type: void
     */
    public Flight(int flightNo, String date, Section[] sections) {
        this.flightNo = flightNo;
        this.date = date;
        this.sections = sections;
        this.manifest = new PassengerOrderedList();
        this.finders = new SeatPlacementFinder[] {
            new TogetherWithPrefsFinder(),
            new TogetherIgnorePrefsFinder(),
            new SinglesFinder()
        };
    }

    /**
     * getFlightNo method
     * param: none
     * returns the flight number
     * return type: int
     */
    public int getFlightNo() {
        return flightNo;
    }

    /**
     * getDate method
     * param: none
     * returns the flight date string (YYYY/MM/DD)
     * return type: String
     */
    public String getDate() { 
        return date;
    }

    /**
     * allocatePid method
     * param: none
     * allocates and returns the next unique passenger ID for this flight
     * return type: int
     */
    public int allocatePid() {
        int p = nextPid;
        nextPid++;
        return p;
    }

    /**
     * findPassenger method
     * param: last (String), first (String), dob (String)
     * looks up and returns a Passenger from the manifest matching name and dob
     * return type: Passenger (or null if not found)
     */
    public Passenger findPassenger(String last, String first, String dob) {
        return manifest.find(last, first, dob);
    }

    /**
     * insertIntoManifest method
     * param: p (Passenger)
     * inserts a Passenger into the flight manifest (ordered list)
     * return type: void
     */
    public void insertIntoManifest(Passenger p) {
        manifest.insertOrdered(p);
    }

    /**
     * reserve method
     * param: sectionIndexOneBased (int), group (Passenger[])
     * attempts to reserve seats for the group in the specified section (1-based).
     * tries finders in order; picks a random placement from candidates if available.
     * return type: boolean (true if reservation succeeded)
     */
    
    public boolean reserve(int sectionIndexOneBased, Passenger[] group) {
        int si = sectionIndexOneBased - 1;
        if (si < 0 || si >= sections.length) {
            System.out.println("ERROR: Section " + sectionIndexOneBased + " invalid for flight " + flightNo);
            return false;
        }
        Section sec = sections[si];
        int i;
        for (i = 0; i < finders.length; i++) {
            SeatPlacementFinder finder = finders[i];
            PlacementList candidates = finder.findPlacements(sec, group, date);
            int n = candidates.size();
            if (n > 0) {
                int pick = RNG.nextInt(n);
                Placement chosen = candidates.get(pick);
                sec.placeGroup(group, chosen, this, si);
                return true;
            }
        }
        System.out.println("ERROR: Cannot seat group on flight " + flightNo + " section " + sectionIndexOneBased);
        return false;
    }

    /**
     * cancel method
     * param: sectionIndexOneBasedOrMinus1 (int), group (Passenger[])
     * validates and cancels the specified passengers; if section index provided it is checked.
     * removes passengers from manifest and frees seats.
     * return type: boolean (true if cancellation succeeded)
     */
    public boolean cancel(int sectionIndexOneBasedOrMinus1, Passenger[] group) {
        int i;
        for (i = 0; i < group.length; i++) {
            Passenger p = group[i];
            Passenger found = findPassenger(p.lastName, p.firstName, p.dob);
            if (found == null) {
                System.out.println("ERROR: Passenger not found for cancellation: " + p.lastName + "," + p.firstName + "," + p.dob + " on flight " + flightNo);
                return false;
            }
            if (sectionIndexOneBasedOrMinus1 != -1) {
                int required = sectionIndexOneBasedOrMinus1 - 1;
                if (found.sectionIndex != required) {
                    System.out.println("ERROR: Passenger " + p.lastName + " not seated in section " + sectionIndexOneBasedOrMinus1);
                    return false;
                }
            }
        }
        // perform removals
        for (i = 0; i < group.length; i++) {
            Passenger p = group[i];
            Passenger removed = manifest.remove(p.lastName, p.firstName, p.dob);
            if (removed != null) {
                int secIdx = removed.sectionIndex;
                if (secIdx >= 0 && secIdx < sections.length) {
                    sections[secIdx].removePassenger(removed.rowNumber, removed.seatIndex);
                }
            } else {
                System.out.println("Warning: Attempted removal but passenger not in manifest: " + p.lastName + "," + p.firstName + "," + p.dob);
            }
        }
        return true;
    }

    /**
     * printSeatingChart method
     * param: none
     * prints the seating chart for the flight (all sections) to standard output
     * return type: void
     */
    public void printSeatingChart() {
        System.out.println("Flight: " + flightNo);
        System.out.println("Date:   " + date);
        int i;
        for (i = 0; i < sections.length; i++) {
            System.out.println();
            System.out.println("Section " + (i + 1) + ":");
            sections[i].printSection(date);
        }
    }

    /**
     * printManifest method
     * param: none
     * prints the flight manifest (passengers in alphabetical order) to standard output
     * return type: void
     */
    public void printManifest() {
        System.out.println();
        System.out.println("Flight Manifest:");
        Object[] arr = manifest.toArray();
        if(arr.length == 0) {
                System.out.println("(Flight is empty)");
            }
        int i = 0;
        while (i < arr.length) {
            Passenger p = (Passenger)arr[i];
            String prefStr = "";
            if (p.preference == 'W') prefStr = "(Window Preference)";
            else if (p.preference == 'A') prefStr = "(Aisle Preference)";
            System.out.printf(" %03d %3d %-12s %-12s %s %s\n", p.passengerID, p.rowNumber, p.lastName, p.firstName, p.dob, prefStr);
            i++;
        }
        System.out.println("---------------------------END OF LIST----------------------------");
    }

    /**
     * compareTo method
     * param: other (OrderedData)
     * compares two Flight objects by flight number
     * return type: int (negative if this < other, 0 if equal, positive if this > other)
     **/
    @Override
    public int compareTo(OrderedData other) {
        Flight f = (Flight) other;
        return this.flightNo - f.flightNo;
    }

    /**
     * toString method
     * param: none
     * returns string representation of the flight
     * return type: String
     **/
    @Override
    public String toString() {
        return "Flight " + flightNo + " (" + date + ")";
    }
}