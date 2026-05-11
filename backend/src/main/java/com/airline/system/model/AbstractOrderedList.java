/**
 * AbstractOrderedList
 *
 * PURPOSE: A singly linked ordered list where subclasses define the ordering by implementing the compare method. Provides functionality to insert in order, 
 * convert to array, remove elements by index, and check size.
 */

package com.airline.system.model;
public abstract class AbstractOrderedList {
    private static class Node {
        OrderedData data;
        Node next;
        Node(OrderedData d) {
            data = d; next = null; 
        }
    }

    private Node head;

    /**
     * Constructor for AbstractOrderedList
     * param: none
     * initializes an empty ordered list
     * return type: none
     */
    public AbstractOrderedList() {
        head = null;
    }

    /**
     * compare method
     * param: a (OrderedData), b (OrderedData)
     * compares two objects for ordering, implemented by subclass
     * return type: int (negative if a < b, 0 if equal, positive if a > b)
     */
    public int compare(OrderedData a, OrderedData b) {
        return a.compareTo(b);
    }

    /**
     * insertOrdered method
     * param: item (OrderedData)
     * inserts an item into the list while maintaining order
     * return type: void
     */
    public void insertOrdered(OrderedData item) {
        Node prev = null;
        Node curr = head;
        while (curr != null && compare(curr.data, item) < 0) {
            prev = curr;
            curr = curr.next;
        }
        Node n = new Node(item);
        if (prev == null) {
            n.next = head;
            head = n;
        } else {
            n.next = prev.next;
            prev.next = n;
        }
    }

    /**
     * toArray method
     * param: none
     * converts the list into an Object array
     * return type: Object[]
     */
    public Object[] toArray() {
        // first count nodes
        int count = 0;
        Node curr = head;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        Object[] arr = new Object[count];
        curr = head;
        int idx = 0;
        while (curr != null) {
            arr[idx] = curr.data;
            idx++;
            curr = curr.next;
        }
        return arr;
    }

    /**
     * removeAtIndex method
     * param: index (int)
     * removes and returns the object at the given index (0-based),
     * returns null if index is out of range
     * return type: Object
     */
    public Object removeAtIndex(int index) {
        if (index < 0) return null;
        Node prev = null;
        Node curr = head;
        int i = 0;
        while (curr != null && i < index) {
            prev = curr;
            curr = curr.next;
            i++;
        }
        if (curr == null) return null;
        if (prev == null) {
            head = curr.next;
        } else {
            prev.next = curr.next;
        }
        curr.next = null;
        return curr.data;
    }

    /**
     * size method
     * param: none
     * calculates and returns the number of elements in the list
     * return type: int
     */
    public int size() {
        int count = 0;
        Node curr = head;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        return count;
    }
}