/**
 * Section
 *
 * PURPOSE: Represents a section of the aircraft: rows, seat blocks, and seat layout.
 */

package com.airline.system.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Section {
    private int rows;
    private int[] blocks;
    private Seat[][] seats;
    private int baseRowNumber;
    private boolean[] emergencyExitRows;

    /**
     * Section constructor
     * param: rows (int), blocks (int[]), baseRowNumber (int), emergencyExitRows (boolean[])
     * builds the seat matrix for the section given block layout and base row number
     * return type: void
     **/
    public Section(int rows, int[] blocks, int baseRowNumber, boolean[] emergencyExitRows) {
        this.rows = rows;
        this.blocks = new int[blocks.length];
        this.emergencyExitRows = emergencyExitRows;

        int i;
        for (i = 0; i < blocks.length; i++) {
            this.blocks[i] = blocks[i];
        }

        this.baseRowNumber = baseRowNumber;

        int perRow = 0;
        for (i = 0; i < this.blocks.length; i++) {
            perRow = perRow + this.blocks[i];
        }

        seats = new Seat[rows][perRow];

        int r;
        for (r = 0; r < rows; r++) {
            int idx = 0;
            int bi;
            for (bi = 0; bi < this.blocks.length; bi++) {
                int blockSize = this.blocks[bi];
                int s;
                for (s = 0; s < blockSize; s++) {
                    boolean isWindow = false;
                    if (idx == 0 || idx == perRow - 1) {
                        isWindow = true;
                    }

                    boolean isAisle = false;
                    // Handle single block case (blocks.length == 1)
                    if (blocks.length == 1) {
                        // In single block, leftmost seat is aisle
                        isAisle = (s == 0);
                    } else {
                        // Normal multi-block logic
                        if ((s == blockSize - 1 && bi < this.blocks.length - 1)
                                || (s == 0 && bi > 0)) {
                            isAisle = true;
                        }
                    }

                    seats[r][idx] = new Seat(baseRowNumber + r, idx, isWindow, isAisle);
                    idx++;
                }
            }
        }
    }

    /**
     * numRows method
     * param: none
     * returns the number of rows in this section
     * return type: int
     **/
    public int numRows() {
        return rows;
    }

    /**
     * seatsPerRow method
     * param: none
     * returns the number of seats per row in this section
     * return type: int
     **/
    public int seatsPerRow() {
        return seats[0].length;
    }

    /**
     * getSeat method
     * param: rowIndex (int), seatIndex (int)
     * returns the Seat object at the specified local row and seat index
     * return type: Seat
     **/
    public Seat getSeat(int rowIndex, int seatIndex) {
        return seats[rowIndex][seatIndex];
    }

    /**
     * isEmergencyExitRow method
     * param: rowIndex (int)
     * returns true if the specified row is an emergency exit row
     * return type: boolean
     **/
    public boolean isEmergencyExitRow(int rowIndex) {
        return emergencyExitRows != null && rowIndex >= 0 && 
               rowIndex < emergencyExitRows.length && emergencyExitRows[rowIndex];
    }

    /**
     * placeGroup method
     * param: group (Passenger[]), p (Placement), flight (Flight), sectionIndex (int)
     * places passengers into seats according to the provided Placement and updates passenger info and manifest
     * return type: void
     **/
    public void placeGroup(Passenger[] group, Placement p, Flight flight, int sectionIndex) {
        if (p == null)
            return;
        if (p.isGroupPlacement()) {
            int r = p.rowIndex;
            int start = p.startSeatIndex;
            int i;
            for (i = 0; i < p.length; i++) {
                Seat s = seats[r][start + i];
                s.occupant = group[i];
                group[i].passengerID = flight.allocatePid();
                group[i].sectionIndex = sectionIndex;
                group[i].rowNumber = s.row;
                group[i].seatIndex = s.index;
                flight.insertIntoManifest(group[i]);
            }
        } else {
            int perRow = seatsPerRow();
            int i;
            for (i = 0; i < p.singleSeats.length; i++) {
                int code = p.singleSeats[i];
                int r = code / perRow;
                int si = code % perRow;
                Seat s = seats[r][si];
                s.occupant = group[i];
                group[i].passengerID = flight.allocatePid();
                group[i].sectionIndex = sectionIndex;
                group[i].rowNumber = s.row;
                group[i].seatIndex = s.index;
                flight.insertIntoManifest(group[i]);
            }
        }
    }

    /**
     * removePassenger method
     * param: absoluteRowNumber (int), seatIndex (int)
     * removes the occupant from the given absolute row number and seat index if valid
     * return type: void
     **/
    public void removePassenger(int absoluteRowNumber, int seatIndex) {
        int local = absoluteRowNumber - baseRowNumber;
        if (local < 0) {
            return;
        }
        if (local >= rows) {
            return;
        }
        if (seatIndex < 0) {
            return;
        }
        if (seatIndex >= seats[local].length) {
            return;
        }
        seats[local][seatIndex].occupant = null;
    }

    /**
     * printSection method
     * param: flightDate (String)
     * prints the seating layout for this section, marking passenger IDs and minors
     * return type: void
     **/
    public void printSection(String flightDate) {
        int r;
        for (r = 0; r < rows; r++) {
            int absRow = baseRowNumber + r;
            String emergencyMark = isEmergencyExitRow(r) ? "E " : "  ";
            System.out.print(emergencyMark);
            if (absRow < 10)
                System.out.print(" ");
            System.out.print(absRow + ": ");
            int accum = 0;
            int bi;
            for (bi = 0; bi < blocks.length; bi++) {
                int blockSize = blocks[bi];
                int s;
                for (s = 0; s < blockSize; s++) {
                    Seat seat = seats[r][accum];
                    if (seat.occupant == null) {
                        System.out.print("[    ]");
                    } else {
                        boolean minor = seat.occupant.isMinorOn(flightDate);
                        String star = " ";
                        if (minor)
                            star = "*";
                        int passengerID = seat.occupant.passengerID;
                        String pidStr = String.valueOf(passengerID);
                        while (pidStr.length() < 3)
                            pidStr = " " + pidStr;
                        System.out.print("[" + pidStr + star + "]");
                    }
                    accum++;
                }
                if (bi < blocks.length - 1) {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }
}