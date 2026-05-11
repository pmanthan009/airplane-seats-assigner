/**
 * SeatSpanFinder
 *
 * PURPOSE: Helpers for neighboring spans to place groups and single-seat combinations.
 */

package com.airline.system.model;
public class SeatSpanFinder {
    private static PlacementList last = new PlacementList();

    /**
     * clearLast method
     * param: none
     * resets internal last PlacementList to an empty list
     * return type: void
     **/
    public static void clearLast() {
        last = new PlacementList();
    }

    /**
     * getLastPlacements method
     * param: none
     * returns the last generated PlacementList (may be empty)
     * return type: PlacementList
     **/
    public static PlacementList getLastPlacements() {
        return last;
    }

    /**
     * addPlacementsForGroup method
     * param: sec (Section), group (Passenger[]), enforcePrefs (boolean), flightDate (String)
     * scans each row of the section and collects all neighboring placements
     * matching the group size and (optionally) seating preferences
     * return type: void
     **/
    public static void addPlacementsForGroup(Section sec, Passenger[] group, boolean enforcePrefs, String flightDate) {
        clearLast();
        int k = group.length;
        if (k <= 0)
            return;
        int rows = sec.numRows();
        int perRow = sec.seatsPerRow();

        // Check if group contains minors
        boolean hasMinors = false;
        for (Passenger p : group) {
            if (p.isMinorOn(flightDate)) {
                hasMinors = true;
                break;
            }
        }

        int r;
        for (r = 0; r < rows; r++) {
            // Skip emergency rows if group has minors
            if (hasMinors && sec.isEmergencyExitRow(r)) {
                continue;
            }
            
            int start;
            for (start = 0; start + k <= perRow; start++) {
                boolean ok = true;
                int t;
                t = 0;
                while (t < k && ok) {
                    Seat s = sec.getSeat(r, start + t);
                    if (s.occupant != null) {
                        ok = false;
                    } else {
                        if (enforcePrefs) {
                            char preference = group[t].preference;
                            if (preference == 'W') {
                                if (s.isWindow == false) {
                                    ok = false;
                                }
                            } else if (preference == 'A') {
                                if (s.isAisle == false) {
                                    ok = false;
                                }
                            }
                        }
                    }
                    t++;
                }
                if (ok) {
                    Placement p = new Placement();
                    p.rowIndex = r;
                    p.startSeatIndex = start;
                    p.length = k;
                    p.matchesPrefs = enforcePrefs;
                    last.add(p);
                }
            }
        }
    }

    /**
     * addSingleSeatPlacements method
     * param: sec (Section), k (int), flightDate (String)
     * builds all combinations of k empty seats in the section and stores them
     * in the internal last PlacementList, ensuring minors are not placed in emergency rows
     * return type: void
     **/
    public static void addSingleSeatPlacements(Section sec, int k, String flightDate) {
        clearLast();
        int rows = sec.numRows();
        int perRow = sec.seatsPerRow();
        int total = rows * perRow;
        
        // Collect all empty seats, but separate minors-capable seats
        int[] allEmpties = new int[total];
        int[] nonEmergencyEmpties = new int[total];
        int allCount = 0;
        int nonEmergencyCount = 0;
        
        int r;
        for (r = 0; r < rows; r++) {
            boolean isEmergencyRow = sec.isEmergencyExitRow(r);
            int s;
            for (s = 0; s < perRow; s++) {
                Seat seat = sec.getSeat(r, s);
                if (seat.occupant == null) {
                    allEmpties[allCount] = r * perRow + s;
                    allCount++;
                    if (!isEmergencyRow) {
                        nonEmergencyEmpties[nonEmergencyCount] = r * perRow + s;
                        nonEmergencyCount++;
                    }
                }
            }
        }

        // Use appropriate array based on whether we need to avoid emergency rows
        int[] empties = allCount == nonEmergencyCount ? allEmpties : nonEmergencyEmpties;
        int emptiesCount = allCount == nonEmergencyCount ? allCount : nonEmergencyCount;
        
        if (emptiesCount < k)
            return;
        int[] idxs = new int[k];
        int i;
        for (i = 0; i < k; i++)
            idxs[i] = i;
        boolean finished = false;
        while (!finished) {
            Placement p = new Placement();
            p.singleSeats = new int[k];
            for (i = 0; i < k; i++) {
                p.singleSeats[i] = empties[idxs[i]];
            }
            last.add(p);
            int t = k - 1;
            while (t >= 0 && idxs[t] == emptiesCount - k + t) {
                t--;
            }
            if (t < 0) {
                finished = true;
            } else {
                idxs[t] = idxs[t] + 1;
                int j;
                for (j = t + 1; j < k; j++) {
                    idxs[j] = idxs[j - 1] + 1;
                }
            }
        }
    }
}