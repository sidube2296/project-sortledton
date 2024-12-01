package edu.uwm.cs351;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * UnrolledSkipList is an implementation of the Neighborhood interface, optimized
 * for handling large neighborhoods using unrolled skip lists. This structure supports
 * efficient scans and intersections with sorted blocks of neighbors.
 *
 * @param <T> The type of the vertex ID
 */
public class UnrolledSkipList<T extends Comparable<T>> implements Neighborhood<T> {
    /** Stores blocks of edges for efficient neighborhood scans and intersections */
    private final ConcurrentSkipListSet<T> blocks;

    /**
     * Constructs an UnrolledSkipList with an empty set of blocks.
     */
    public UnrolledSkipList() {
        this.blocks = new ConcurrentSkipListSet<>();
    }

    /**
     * Adds a neighbor to the neighborhood, inserting into the appropriate block.
     *
     * @param id The ID of the neighbor to add.
     */
    public void addNeighbor(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Neighbor ID cannot be null");
        }
        blocks.add(id);
    }

    /**
     * Removes a neighbor from the neighborhood, updating the block structure as necessary.
     *
     * @param id The ID of the neighbor to remove.
     */
    public void removeNeighbor(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Neighbor ID cannot be null");
        }
        blocks.remove(id);
    }

    /**
     * Retrieves all neighbors in the neighborhood.
     *
     * @return A list of IDs representing neighbors in sorted order.
     */
    public List<T> getNeighbors() {
        return new ArrayList<>(blocks);
    }

    /**
     * Finds the intersection between this neighborhood and another unrolled skip list.
     *
     * @param other The other UnrolledSkipList to intersect with.
     * @return A list of IDs representing common neighbors.
     */
    public List<T> intersect(Neighborhood<T> other) {
        if (other == null) {
            throw new IllegalArgumentException("Other neighborhood cannot be null");
        }

        List<T> result = new ArrayList<>();
        for (T neighbor : blocks) {
            if (other.getNeighbors().contains(neighbor)) {
                result.add(neighbor);
            }
        }
        return result;
    }
}
