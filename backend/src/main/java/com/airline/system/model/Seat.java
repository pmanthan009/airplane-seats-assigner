/**
 * Seat
 *
 * PURPOSE: Represents a seat on the plane with position, type, and occupant information.
 */

package com.airline.system.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Seat {
    public final int row;   // absolute row number
    public final int index; // seat index in row
    public final boolean isWindow;
    public final boolean isAisle;
    public Passenger occupant; // null if empty

    public Seat(int row, int index, boolean isWindow, boolean isAisle) {
        this.row = row;
        this.index = index;
        this.isWindow = isWindow;
        this.isAisle = isAisle;
        this.occupant = null;
    }
}
