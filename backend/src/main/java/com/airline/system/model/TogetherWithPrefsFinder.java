/**
 * TogetherWithPrefsFinder
 *
 * PURPOSE: Finder that places groups together while enforcing seating preferences (First preference of the assignment).
 */

package com.airline.system.model;
public class TogetherWithPrefsFinder extends SeatPlacementFinder {

    /**
     * findPlacements method
     * param: sec (Section), group (Passenger[]), flightDate (String)
     * finds all group placements in the section that respect passenger seating preferences
     * return type: PlacementList
     **/
    @Override
    public PlacementList findPlacements(Section sec, Passenger[] group, String flightDate) {
        return collectGroupPlacements(sec, group, true, true, flightDate);
    }
}