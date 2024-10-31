package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.List;

/**
 * PowerOfTwoVector is an implementation of the Neighborhood interface, optimized
 * for handling small neighborhoods by using power-of-two-sized vectors. This
 * structure is sorted for efficient intersection operations and neighborhood scans.
 *
 * @param <T> The type of the vertex ID
 */
public class PowerofTwo<T> implements Neighborhood<T>{

	/** The list to store neighbors, kept sorted */
    private final List<T> neighbors;

    /**
     * Constructs a new PowerOfTwoVector with an empty list of neighbors.
     */
    public PowerofTwo() {
        this.neighbors = new ArrayList<>();
    }

    /**
     * Adds a neighbor to the neighborhood, keeping the vector sorted.
     *
     * @param id The ID of the neighbor to add.
     */
    public void addNeighbor(T id) { /*...*/ }

    /**
     * Removes a neighbor from the neighborhood if it exists.
     *
     * @param id The ID of the neighbor to remove.
     */
    public void removeNeighbor(T id) { /*...*/ }

    /**
     * Retrieves all neighbors in the neighborhood.
     *
     * @return A sorted list of neighbor IDs.
     */
    public List<T> getNeighbors() {
    	/*...*/
    	return null;
    }

    /**
     * Finds the intersection between this neighborhood and another power-of-two vector.
     *
     * @param other The other PowerOfTwoVector to intersect with.
     * @return A list of IDs representing common neighbors.
     */
    public List<T> intersect(Neighborhood<T> other) {
    	/*...*/ 
    	return null;
    }
}
