package edu.uwm.cs351;

import java.util.List;

/**
 * Neighborhood is an interface representing the neighborhood structure for a vertex.
 * It provides methods for adding, removing, and retrieving neighbors, as well as
 * finding intersections with other neighborhoods. This interface can be implemented
 * by classes optimized for handling small or large neighborhoods, such as
 * {@link PowerofTwo} (for smaller sets) and {@link UnrolledSkipList} (for larger sets).
 *
 * @param <T> The type of the vertex ID, must be Comparable.
 */
public interface Neighborhood<T extends Comparable<T>> {

    /**
     * Adds a neighbor to this neighborhood.
     *
     * @param id The ID of the neighbor to add.
     * @throws IllegalArgumentException if id is null.
     */
    void addNeighbor(T id);

    /**
     * Removes a neighbor from this neighborhood.
     *
     * @param id The ID of the neighbor to remove.
     * @throws IllegalArgumentException if id is null.
     */
    void removeNeighbor(T id);

    /**
     * Retrieves all neighbors in this neighborhood.
     *
     * @return A list of IDs representing all neighbors in sorted order.
     */
    List<T> getNeighbors();

    /**
     * Finds the intersection of this neighborhood with another neighborhood.
     *
     * @param other The other neighborhood to intersect with.
     * @return A list of IDs representing common neighbors.
     * @throws IllegalArgumentException if other is null.
     */
    List<T> intersect(Neighborhood<T> other);
    
    /**
     * Checks if a neighbor exists in this neighborhood.
     *
     * @param id The ID of the neighbor to check.
     * @return True if the neighbor exists, otherwise false.
     * @throws IllegalArgumentException if id is null.
     */
     boolean contains(T id);
}
