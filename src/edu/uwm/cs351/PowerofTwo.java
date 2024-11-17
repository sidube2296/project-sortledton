package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PowerOfTwoVector is an implementation of the Neighborhood interface, optimized
 * for handling small neighborhoods by using power-of-two-sized vectors. This
 * structure is sorted for efficient intersection operations and neighborhood scans.
 *
 * @param <T> The type of the vertex ID
 */
public class PowerofTwo<T extends Comparable<T>> implements Neighborhood<T> {

    /** The list to store neighbors, kept sorted */
    private final List<T> neighbors;

    /**
     * Constructs a new PowerOfTwoVector with an empty list of neighbors.
     */
    public PowerofTwo() {
        this.neighbors = new ArrayList<>();
    }
    
    /**
     * Returns the number of neighbors in the neighborhood.
     *
     * @return The size of the neighborhood.
     */
    public int size() {
        return neighbors.size();
    }

    /**
     * Adds a neighbor to the neighborhood, keeping the vector sorted.
     *
     * @param id The ID of the neighbor to add.
     */
    @Override
    public void addNeighbor(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        int index = Collections.binarySearch(neighbors, id);
        if (index < 0) {
            neighbors.add(-index - 1, id); // Insert while maintaining sorted order
        }
    }

    /**
     * Removes a neighbor from the neighborhood if it exists.
     *
     * @param id The ID of the neighbor to remove.
     */
    @Override
    public void removeNeighbor(T id) {
        int index = Collections.binarySearch(neighbors, id);
        if (index >= 0) {
            neighbors.remove(index);
        }
    }

    /**
     * Retrieves all neighbors in the neighborhood.
     *
     * @return A sorted list of neighbor IDs.
     */
    @Override
    public List<T> getNeighbors() {
        return new ArrayList<>(neighbors); // Return a copy to prevent modification
    }

    /**
     * Finds the intersection between this neighborhood and another power-of-two vector.
     *
     * @param other The other Neighborhood to intersect with.
     * @return A list of IDs representing common neighbors.
     */
    @Override
    public List<T> intersect(Neighborhood<T> other) {
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
        return intersection;
    }
}