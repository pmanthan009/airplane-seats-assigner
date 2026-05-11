/**
 * SeatPlacementFinder
 *
 * PURPOSE: Abstract class for finding possible seat placements in a section.
 */

package com.airline.system.model;
public abstract class SeatPlacementFinder {
    /**
     * findPlacements method (abstract)
     * param: sec (Section), group (Passenger[]), flightDate (String)
     * must be implemented by subclasses to return possible placements 
     * according to a specific seating policy
     * return type: PlacementList
     **/
    public abstract PlacementList findPlacements(Section sec, Passenger[] group, String flightDate);

    /**
     * collectGroupPlacements method
     * param: sec (Section), group (Passenger[]), enforcePrefs (boolean), frontToBack (boolean), flightDate (String)
     * scans rows in given order and collects all possible neighboring placements
     * matching group size and seating preferences
     * return type: PlacementList
     **/
    public PlacementList collectGroupPlacements(Section sec, Passenger[] group, boolean enforcePrefs,
            boolean frontToBack, String flightDate) {
        PlacementList results = new PlacementList();
        int k = group.length;
        if (k <= 0) {
            return results;
        }
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

        int rStart;
        int rEnd;
        int rStep;
        if (frontToBack) {
            rStart = 0;
            rEnd = rows - 1;
            rStep = 1;
        } else {
            rStart = rows - 1;
            rEnd = 0;
            rStep = -1;
        }

        int r = rStart;
        boolean finished = false;

        while (!finished) {
            // Skip emergency exit rows if group has minors
            if (!hasMinors || !sec.isEmergencyExitRow(r)) {
                int start = 0;
                while (start + k <= perRow) {
                    boolean ok = true;
                    int t = 0;
                    while (t < k && ok) {
                        Seat s = sec.getSeat(r, start + t);
                        if (s.occupant != null) {
                            ok = false;
                        } else if (enforcePrefs) {
                            char preference = group[t].preference;
                            if (preference == 'W') {
                                if (!s.isWindow) {
                                    ok = false;
                                }
                            } else if (preference == 'A') {
                                if (!s.isAisle) {
                                    ok = false;
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
                        results.add(p);
                    }
                    start++;
                }
            }

            if (r == rEnd) {
                finished = true;
            } else {
                r = r + rStep;
            }
        }
        return results;
    }
}