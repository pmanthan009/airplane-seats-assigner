/**
 * Passenger
 *
 * PURPOSE: Represents a passenger with personal details, seating info, and methods to calculate age and minor status.
 */

package com.airline.system.model;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class Passenger extends OrderedData {
    public int passengerID = -1;
    public String lastName;
    public String firstName;
    public String dob; // YYYY/MM/DD
    public char preference;  // 'W', 'A'

    // seating
    public int sectionIndex = -1; // 0-based
    public int rowNumber = -1;    // absolute
    public int seatIndex = -1;    // index within row

    /**
     * Passenger constructor
     * param: last (String), first (String), dob (String), preference (char)
     * initializes passenger details including name, date of birth, and seating preference
     * return type: none
     **/
    public Passenger(String last, String first, String dob, char preference) {
        this.lastName = last;
        this.firstName = first;
        this.dob = dob;
        this.preference = preference;
    }

    /**
     * ageOn method
     * param: date (String in YYYY/MM/DD format)
     * calculates the age of the passenger on the given date
     * return type: int (age in years)
     **/
    public int ageOn(String date) {
        String[] a = dob.split("/");
        String[] b = date.split("/");
        int y1 = Integer.parseInt(a[0]);
        int m1 = Integer.parseInt(a[1]);
        int d1 = Integer.parseInt(a[2]);
        int y2 = Integer.parseInt(b[0]);
        int m2 = Integer.parseInt(b[1]);
        int d2 = Integer.parseInt(b[2]);
        int age = y2 - y1;
        if (m2 < m1) {
            age--;
        } else if (m2 == m1) {
            if (d2 < d1) {
                age--;
            }
        }
        return age;
    }

    /**
     * isMinorOn method
     * param: date (String in YYYY/MM/DD format)
     * determines if the passenger is a minor (under 16 years old) on the given date
     * return type: boolean
     **/
    public boolean isMinorOn(String date) {
        int a = ageOn(date);
        if (a < 16) return true;
        return false;
    }

    /**
     * compareTo method
     * param: other (OrderedData)
     * compares two Passenger objects by last name, then first name
     * return type: int (negative if this < other, 0 if equal, positive if this > other)
     **/
    @Override
    public int compareTo(OrderedData other) {
        Passenger p = (Passenger) other;
        int cmp = lastName.compareTo(p.lastName);
        if (cmp != 0) return cmp;
        return firstName.compareTo(p.firstName);
    }

    /**
     * toString method
     * param: none
     * returns string representation of the passenger
     * return type: String
     **/
    @Override
    public String toString() {
        return lastName + ", " + firstName + " (" + dob + ")";
    }
}