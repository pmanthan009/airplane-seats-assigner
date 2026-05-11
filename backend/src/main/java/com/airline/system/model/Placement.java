/**
 * Placement
 *
 * PURPOSE: Represents either a group seating placement in a row or individual single-seat assignments.
 */

package com.airline.system.model;
public class Placement {
    // group in one row
    public int rowIndex = -1;       // local row index (0..rows-1)
    public int startSeatIndex = -1;
    public int length = 0;

    // singles
    public int[] singleSeats = null; // encoded = rowIndex * seatsPerRow + seatIndex

    public boolean matchesPrefs = false;

    public boolean isGroupPlacement() {
        if (rowIndex >= 0) {
            return true;
        }
        return false;
    }
}
