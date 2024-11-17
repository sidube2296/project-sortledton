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
	 * Returns the current number of neighbors in the neighborhood.
	 *
	 * @return The number of neighbors in the neighborhood.
	 */
	public Object size() {
		return neighbors.size();
	}

	/**
	 * Adds a neighbor (edge) to the neighborhood, keeping the vector sorted.
	 * If the neighborhood's current capacity is insufficient, it resizes
	 * to the next power of two.
	 *
	 * @param id The ID of the neighbor to add.
	 * @throws IllegalArgumentException if the neighbor ID is {@code null}
	 */
	public void addNeighbor(T id) { /*...*/ }

	/**
	 * Removes a specified neighbor from the neighborhood, if it exists.
	 *
	 * @param id The ID of the neighbor to remove.
	 * @throws IllegalArgumentException if the neighbor ID is {@code null}
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
	 * Finds the intersection between this neighborhood and another Neighborhood.
	 * Returns a sorted list of shared neighbor IDs.
	 *
	 * @param other The other Neighborhood to intersect with.
	 * @return A list of IDs representing common neighbors.
	 * @throws IllegalArgumentException if the other neighborhood is {@code null}
	 */
	public List<T> intersect(Neighborhood<T> other) {
		/*...*/ 
		return null;
	}
}
