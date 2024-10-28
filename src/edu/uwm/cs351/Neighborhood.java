package edu.uwm.cs351;

import java.util.List;
/**
 * Neighborhood is an interface representing the neighborhood structure for a vertex.
 * It provides methods for adding, removing, and retrieving neighbors, as well as
 * finding intersections with other neighborhoods. This interface is implemented by
 * both PowerOfTwoVector and UnrolledSkipList for handling small and large neighborhoods, respectively.
 *
 * @param <T> The type of the vertex ID
 */
public interface Neighborhood<T> {

	/**
     * Adds a neighbor to this neighborhood.
     *
     * @param id The ID of the neighbor to add.
     */
    public void addNeighbor(T id);

    /**
     * Removes a neighbor from this neighborhood.
     *
     * @param id The ID of the neighbor to remove.
     */
    public void removeNeighbor(T id);

    /**
     * Retrieves all neighbors in this neighborhood.
     *
     * @return A list of IDs representing all neighbors.
     */
    public List<T> getNeighbors();

    /**
     * Finds the intersection of this neighborhood with another neighborhood.
     *
     * @param other The other neighborhood to intersect with.
     * @return A list of IDs representing common neighbors.
     */
    public List<T> intersect(Neighborhood<T> other);
}
