/**
 * TogetherIgnorePrefsFinder
 *
 * PURPOSE: Finder that places groups together while ignoring seating preferences (Second preference of the assignment).
 */

package com.airline.system.model;
public class TogetherIgnorePrefsFinder extends SeatPlacementFinder {

    /**
     * findPlacements method
     * param: sec (Section), group (Passenger[]), flightDate (String)
     * finds all group placements in the section without enforcing passenger seating preferences
     * return type: PlacementList
     **/
    @Override
    public PlacementList findPlacements(Section sec, Passenger[] group, String flightDate) {
        return collectGroupPlacements(sec, group, false, true, flightDate);
    }
}