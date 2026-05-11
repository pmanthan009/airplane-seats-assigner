/**
 * OrderedData
 *
 * PURPOSE: Abstract base class for data that can be stored in ordered lists,
 * providing polymorphic comparison and string representation.
 */

package com.airline.system.model;
public abstract class OrderedData {
    /**
     * compareTo method (abstract)
     * param: other (OrderedData)
     * must be implemented by subclasses to compare two objects for ordering
     * return type: int (negative if this < other, 0 if equal, positive if this > other)
     **/
    public abstract int compareTo(OrderedData other);
    
    /**
     * toString method (abstract)
     * param: none
     * must be implemented by subclasses to provide string representation
     * return type: String
     **/
    public abstract String toString();
}