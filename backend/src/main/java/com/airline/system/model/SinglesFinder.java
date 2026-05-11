/**
 * SinglesFinder
 *
 * PURPOSE: Finder that assigns passengers to individual seats (not necessarily together, final preference of the assignment).
 */

package com.airline.system.model;
public class SinglesFinder extends SeatPlacementFinder {

    /**
     * findPlacements method
     * param: sec (Section), group (Passenger[]), flightDate (String)
     * finds possible single seat placements for each passenger in the group using SeatSpanFinder
     * return type: PlacementList
     **/
    @Override
    public PlacementList findPlacements(Section sec, Passenger[] group, String flightDate) {
        SeatSpanFinder.clearLast();
        SeatSpanFinder.addSingleSeatPlacements(sec, group.length, flightDate);
        return SeatSpanFinder.getLastPlacements();
    }
}