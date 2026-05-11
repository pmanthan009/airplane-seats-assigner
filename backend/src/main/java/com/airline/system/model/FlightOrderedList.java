/**
 * FlightOrderedList
 *
 * PURPOSE: Maintains an ordered list of flights sorted by flight number.
 */

package com.airline.system.model;
public class FlightOrderedList extends AbstractOrderedList {

    /**
     * insertOrdered method
     * param: f (Flight)
     * inserts a Flight into the list while maintaining order
     * return type: void
     **/
    public void insertOrdered(Flight f) {
        super.insertOrdered(f);
    }

    /**
     * find method
     * param: flightNo (int)
     * searches for a Flight by flight number
     * return type: Flight (or null if not found)
     **/
    public Flight find(int flightNo) {
        Object[] arr = super.toArray();
        int i = 0;
        while (i < arr.length) {
            Flight f = (Flight)arr[i];
            if (f.getFlightNo() == flightNo) {
                return f;
            }
            i++;
        }
        return null;
    }
}