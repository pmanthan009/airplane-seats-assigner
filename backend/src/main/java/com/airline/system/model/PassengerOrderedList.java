/**
 * PassengerOrderedList
 *
 * PURPOSE: Maintains an ordered list of passengers sorted by last and first name.
 */

package com.airline.system.model;
public class PassengerOrderedList extends AbstractOrderedList {

    /**
     * insertOrdered method
     * param: p (Passenger)
     * inserts a Passenger into the list while maintaining order
     * return type: void
     **/
    public void insertOrdered(Passenger p) {
        super.insertOrdered(p);
    }

    /**
     * find method
     * param: last (String), first (String), dob (String)
     * searches for a Passenger by last name, first name, and date of birth
     * return type: Passenger (or null if not found)
     **/
    public Passenger find(String last, String first, String dob) {
        Object[] arr = super.toArray();
        int i = 0;
        while (i < arr.length) {
            Passenger p = (Passenger)arr[i];
            if (p.lastName.equals(last) && p.firstName.equals(first) && p.dob.equals(dob)) {
                return p;
            }
            i++;
        }
        return null;
    }

    /**
     * remove method
     * param: last (String), first (String), dob (String)
     * removes a Passenger by last name, first name, and date of birth
     * return type: Passenger (removed passenger or null if not found)
     **/
    public Passenger remove(String last, String first, String dob) {
        Object[] arr = super.toArray();
        int i = 0;
        while (i < arr.length) {
            Passenger p = (Passenger)arr[i];
            if (p.lastName.equals(last) && p.firstName.equals(first) && p.dob.equals(dob)) {
                Object removed = super.removeAtIndex(i);
                if (removed == null) {
                    return null;
                }
                return (Passenger)removed;
            }
            i++;
        }
        return null;
    }
}