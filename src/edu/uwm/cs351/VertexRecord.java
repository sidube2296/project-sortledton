package edu.uwm.cs351;

import java.util.List;

/**
 * VertexRecord represents a vertex's neighborhood and manages access to
 * its neighborhood structure.
 *
 * @param <T> The type of the vertex ID
 */
public class VertexRecord<T> {

	/** The neighborhood data structure, implemented using either PowerOfTwoVector or UnrolledSkipList */
    private final Neighborhood<T> neighborhood;

    /** The size of the neighborhood */
    private final int neighborhoodSize;

    /**
     * Constructs a VertexRecord with an initial neighborhood.
     *
     * @param neighborhood The neighborhood data structure.
     * @param neighborhoodSize The initial size of the neighborhood.
     */
    public VertexRecord() {
        neighborhood = new PowerofTwo<>(); //TODO placeholder for later implementation
        neighborhoodSize = 0; //TODO placeholder for later implementation
    }

    /**
     * Returns the neighborhood of this vertex.
     *
     * @return A list of IDs representing the neighbors.
     */
    public List<T> getNeighborhood() {
    	/*...*/ 
    	return null;
    }

    /**
     * Adds a neighbor to this vertex’s neighborhood.
     *
     * @param id The ID of the neighbor to add.
     */
    public void addNeighbor(T id) { /*...*/ }

    /**
     * Removes a neighbor from this vertex’s neighborhood.
     *
     * @param id The ID of the neighbor to remove.
     */
    public void removeNeighbor(T id) { /*...*/ }
}
