package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PowerofTwo is an implementation of the Neighborhood interface, optimized
 * for handling small neighborhoods by using a sorted list to facilitate 
 * binary search for insertion and intersection. 
 *
 * The structure maintains a sorted list of neighbors for efficient 
 * intersection and lookups. Duplicates are not added.
 *
 * @param <T> The type of the vertex ID, must be Comparable.
 */
public class PowerofTwo<T extends Comparable<T>> implements Neighborhood<T> {

    /** The list to store neighbors, kept sorted. */
    private final List<T> neighbors;

    /**
     * Constructs a new PowerofTwo with an empty list of neighbors.
     * After construction, we assert the invariants.
     */
    public PowerofTwo() {
        this.neighbors = new ArrayList<>();
        assert wellformed();
    }

    /**
     * Ensure the internal invariants hold:
     *  - neighbors is not null.
     *  - neighbors contains no null elements.
     *  - neighbors is strictly sorted in ascending order (implies no duplicates).
     */
    private boolean wellformed() {
        if (neighbors == null) return false;
        for (int i = 0; i < neighbors.size(); i++) {
            T elem = neighbors.get(i);
            if (elem == null) return false;
            if (i > 0) {
                // Check strict ascending order: prev < current
                if (neighbors.get(i-1).compareTo(elem) >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the number of neighbors in the neighborhood.
     * We assert wellformed at start and end to ensure invariants hold continuously.
     *
     * @return The size of the neighborhood.
     */
    public int size() {
        assert wellformed();
        int s = neighbors.size();
        assert wellformed();
        return s;
    }

    /**
     * Adds a neighbor to the neighborhood, keeping the list sorted.
     * If the element already exists, it will not be added again.
     *
     * @param id The ID of the neighbor to add.
     * @throws IllegalArgumentException if the element is null.
     */
    @Override
    public void addNeighbor(T id) {
        assert wellformed();
        if (id == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        int index = Collections.binarySearch(neighbors, id);
        if (index < 0) {
            // Insert while maintaining sorted order
            neighbors.add(-index - 1, id);
        }
        assert wellformed();
    }

    /**
     * Removes a neighbor from the neighborhood if it exists.
     *
     * @param id The ID of the neighbor to remove.
     */
    @Override
    public void removeNeighbor(T id) {
        assert wellformed();
        int index = Collections.binarySearch(neighbors, id);
        if (index >= 0) {
            neighbors.remove(index);
        }
        assert wellformed();
    }

    /**
     * Retrieves all neighbors in the neighborhood as a new list.
     * This ensures the original cannot be modified externally.
     *
     * @return A sorted list of neighbor IDs.
     */
    @Override
    public List<T> getNeighbors() {
        assert wellformed();
        List<T> result = new ArrayList<>(neighbors);
        assert wellformed();
        return result;
    }

    /**
     * Finds the intersection between this neighborhood and another.
     * The intersection will contain only those elements present in both.
     *
     * @param other The other Neighborhood to intersect with.
     * @return A list of IDs representing common neighbors.
     */
    @Override
    public List<T> intersect(Neighborhood<T> other) {
        assert wellformed();
        List<T> intersection = new ArrayList<>();
        List<T> otherNeighbors = other.getNeighbors();
        int i = 0, j = 0;
        while (i < neighbors.size() && j < otherNeighbors.size()) {
            T a = neighbors.get(i);
            T b = otherNeighbors.get(j);
            int comparison = a.compareTo(b);
            if (comparison == 0) {
                intersection.add(a);
                i++;
                j++;
            } else if (comparison < 0) {
                i++;
            } else {
                j++;
            }
        }
        assert wellformed();
        return intersection;
    }
}
