/**
 * PlacementList
 *
 * PURPOSE: Implements a linked list structure for storing Placement objects.
 */

package com.airline.system.model;
public class PlacementList {
    private static class PlNode {
        Placement p;
        PlNode next;
        PlNode(Placement x) {
            p = x;
            next = null;
        }
    }
    private PlNode head = null;
    private PlNode tail = null;
    private int size = 0;

    /**
     * PlacementList constructor
     * Initializes an empty linked list of Placement objects
     * return type: void
     **/
    public PlacementList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * add method
     * param: p (Placement)
     * adds a Placement object to the end of the list
     * return type: void
     **/
    public void add(Placement p) {
        PlNode n = new PlNode(p);
        if (head == null) {
            head = n;
            tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    /**
     * size method
     * returns the number of Placement objects in the list
     * return type: int
     **/
    public int size() {
        return size;
    }

    /**
     * get method
     * param: idx (int)
     * retrieves the Placement object at the specified index or null if invalid
     * return type: Placement
     **/
    public Placement get(int idx) {
        if (idx < 0) return null;
        PlNode curr = head;
        int i = 0;
        while (curr != null && i < idx) {
            curr = curr.next;
            i++;
        }
        if (curr == null) return null;
        return curr.p;
    }
}
