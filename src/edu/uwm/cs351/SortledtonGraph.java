package edu.uwm.cs351;

import java.util.*;
import java.util.function.Consumer;
import java.lang.reflect.Array;

/**
 * SortledtonGraph is the main class for managing the Sortledton graph data structure.
 * This graph structure supports efficient neighborhood access for both small and large
 * neighborhoods by using power-of-two vectors and unrolled skip lists.
 * 
 * This class provides operations for adding and removing vertices and edges, retrieving
 * neighborhoods, and finding intersections between neighborhoods.
 * 
 * <p>Sources:</p>
 * <ul>
 *     <li>Sortledton C++ implementation by Per Fuchs et al: 
 *     <a href= "https://gitlab.db.in.tum.de/per.fuchs/sortledton"> Sortledton GitLab repository</a></li>
 * </ul>
 *
 * @param <T> The type of the vertex ID
 */
public class SortledtonGraph<T extends Comparable<T>> {
    // Constants
    private static final int INITIAL_VECTOR_SIZE = 131072; // Based on authors' implementation
    private static final int BLOCK_SIZE = 128;              // Threshold to switch between Neighborhood types

    // Fields
    private int vertexCount = 0;
    private HashMap<Integer, Integer> logicalToPhysical;   // Maps logical IDs to physical indices
    private Integer[] physicalToLogical;                    // Maps physical indices to logical IDs
    private VertexRecord<T>[] adjacencyIndex;               // Adjacency Index, mapping physical indices to VertexRecords

    private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: " + s);
    private boolean debug = true; // Set to false to disable invariant checks

    /**
     * Sets the debug mode.
     * @param debug true to enable invariant checks, false to disable.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private boolean report(String error) {
        reporter.accept(error);
        return false;
    }

    /**
     * Checks that the SortledtonGraph invariant is correctly adhered to.
     * 
     * @return true when in compliance with all listed invariants
     */
    private boolean wellFormed() {
        if (!debug) return true; // Skip invariant checks if debug is disabled

        // 1. Check for null data structures
        if (logicalToPhysical == null || physicalToLogical == null || adjacencyIndex == null) {
            return report("Data structures for graph must not be null.");
        }

        // 2. Verify logicalToPhysical and physicalToLogical mappings
        for (Map.Entry<Integer, Integer> entry : logicalToPhysical.entrySet()) {
            Integer logicalID = entry.getKey();
            Integer physicalIndex = entry.getValue();

            if (physicalIndex == null || physicalIndex < 0 || physicalIndex >= vertexCount) {
                return report("Invalid physical index for logical ID: " + logicalID);
            }

            if (!logicalToPhysical.containsKey(logicalID)) {
                return report("Logical ID " + logicalID + " in logicalToPhysical is not mapped correctly.");
            }

            if (physicalToLogical[physicalIndex] == null || !physicalToLogical[physicalIndex].equals(logicalID)) {
                return report("Mismatch between logicalToPhysical and physicalToLogical for logical ID: " + logicalID);
            }
        }

        // 3. Check all entries in adjacencyIndex
        for (int i = 0; i < vertexCount; i++) {
            VertexRecord<T> ve = adjacencyIndex[i];
            if (ve != null) {
                // a. Adjacency set must not be null
                if (ve.adjacencySet == null) {
                    return report("Adjacency set is null for vertex at physical index: " + i);
                }

                // b. Adjacency set size must be non-negative
                if (ve.adjacencySetSize < 0) {
                    return report("Negative adjacency set size at index " + i);
                }

                // c. Logical ID in VertexRecord must match mapping
                if (!logicalToPhysical.containsKey(ve.logicalId)) {
                    return report("Logical ID " + ve.logicalId + " in VertexRecord is not in logicalToPhysical.");
                }

                if (!logicalToPhysical.get(ve.logicalId).equals(i)) {
                    return report("Physical index mismatch for logical ID: " + ve.logicalId);
                }

                // d. Adjacency set size must match actual number of neighbors
                if (ve.adjacencySetSize != ve.adjacencySet.getNeighbors().size()) {
                    return report("Adjacency set size mismatch for vertex at physical index: " + i);
                }
            }
        }

        // 4. Vertex count must match the size of logicalToPhysical
        if (vertexCount != logicalToPhysical.size()) {
            return report("Vertex count does not match the number of entries in logicalToPhysical.");
        }

        return true;
    }

    /**
     * Constructs a new SortledtonGraph with an empty adjacency index.
     */
    @SuppressWarnings("unchecked")
    public SortledtonGraph() {
        adjacencyIndex = (VertexRecord<T>[]) new VertexRecord[INITIAL_VECTOR_SIZE];
        physicalToLogical = new Integer[INITIAL_VECTOR_SIZE];
        logicalToPhysical = new HashMap<>(INITIAL_VECTOR_SIZE * 2); // Prevent rehashing
        assert wellFormed() : "Invariant failed at end of SortledtonGraph constructor.";
    }

    /**
     * Getter for vertex count
     * 
     * @return the total number of vertices in the graph
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Retrieves the neighbors of the given vertex.
     *
     * @param vertexId The vertex ID for which to retrieve neighbors.
     * @return A list of IDs representing the neighbors of the specified vertex,
     *         or an empty list if the vertex has no neighbors or does not exist.
     * @throws IllegalArgumentException if the vertex ID is null or does not exist.
     */
    public List<T> getNeighbors(T vertexId) {
        if (vertexId == null) {
            throw new IllegalArgumentException("Vertex ID cannot be null.");
        }

        Integer logicalID = vertexId.hashCode();
        Integer physicalID = logicalToPhysical.get(logicalID);
        if (physicalID == null || physicalID >= vertexCount || physicalID < 0) {
            throw new IllegalArgumentException("Vertex does not exist: " + vertexId);
        }

        VertexRecord<T> assocVR = adjacencyIndex[physicalID];
        if (assocVR == null || assocVR.adjacencySet == null) {
            throw new IllegalArgumentException("Adjacency set is not initialized for vertex: " + vertexId);
        }

        return assocVR.adjacencySet.getNeighbors();
    }

    /**
     * Inserts an edge between two vertices. Creates vertices automatically if they don’t already exist.
     *
     * @param srcId  The source vertex ID.
     * @param destId The destination vertex ID.
     * @throws IllegalArgumentException if either ID is null
     */
    public void insertEdge(T srcId, T destId) { 
        if (srcId == null || destId == null) {
            throw new IllegalArgumentException("@insertEdge, the parameters, srcID and destID may not be null.");
        }
        assert wellFormed() : "Invariant failed at start of insertEdge.";

        // Ensure both vertices exist
        int srcLogicalId = srcId.hashCode();
        int destLogicalId = destId.hashCode();
        if (!logicalToPhysical.containsKey(srcLogicalId)) insertVertex(srcId);
        if (!logicalToPhysical.containsKey(destLogicalId)) insertVertex(destId);

        // Retrieve physical IDs for both vertices
        int srcPhysicalId = logicalToPhysical.get(srcLogicalId);
        int destPhysicalId = logicalToPhysical.get(destLogicalId);

        // Update adjacencyIndex for srcId
        VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];
        if (!srcRecord.adjacencySet.contains(destId)) { 	// Efficient check
            srcRecord.adjacencySet.addNeighbor(destId);
            srcRecord.adjacencySetSize++;
        }

        // Update adjacencyIndex for destId
        VertexRecord<T> destRecord = adjacencyIndex[destPhysicalId];
        if (!destRecord.adjacencySet.contains(srcId)) {	// Efficient check
            destRecord.adjacencySet.addNeighbor(srcId);
            destRecord.adjacencySetSize++;
        }

        // Check for conversion to UnrolledSkipList
        if (srcRecord.adjacencySetSize >= BLOCK_SIZE) convertToUnrolledSkipList(srcPhysicalId);
        if (destRecord.adjacencySetSize >= BLOCK_SIZE) convertToUnrolledSkipList(destPhysicalId);

        assert wellFormed() : "Invariant failed at end of insertEdge.";
    }

    /**
     * Deletes an edge between two vertices if it exists.
     *
     * @param srcId  The source vertex ID.
     * @param destId The destination vertex ID.
     * @throws IllegalArgumentException if either ID is null or if one of the vertices does not exist.
     */
    public void deleteEdge(T srcId, T destId) { 
        if (srcId == null || destId == null) {
            throw new IllegalArgumentException("@deleteEdge, the parameters, srcID and destID may not be null.");
        }

        assert wellFormed() : "Invariant failed at start of deleteEdge.";

        // Ensure both vertices exist
        int srcLogicalId = srcId.hashCode();
        int destLogicalId = destId.hashCode();
        Integer srcPhysicalIdObj = logicalToPhysical.get(srcLogicalId);
        Integer destPhysicalIdObj = logicalToPhysical.get(destLogicalId);

        if (srcPhysicalIdObj == null || destPhysicalIdObj == null) {
            throw new IllegalArgumentException("One or both vertices do not exist in the current state.");
        }

        int srcPhysicalId = srcPhysicalIdObj;
        int destPhysicalId = destPhysicalIdObj;

        // Retrieve the vertex records
        VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];
        VertexRecord<T> destRecord = adjacencyIndex[destPhysicalId];

        // Remove the destination vertex from the source vertex's neighborhood
        Neighborhood<T> srcNeighborhood = srcRecord.adjacencySet;
        if (srcNeighborhood.contains(destId)) { // Efficient check
            srcNeighborhood.removeNeighbor(destId);
            srcRecord.adjacencySetSize--;
        }

        // Remove the source vertex from the destination vertex's neighborhood
        Neighborhood<T> destNeighborhood = destRecord.adjacencySet;
        if (destNeighborhood.contains(srcId)) { // Efficient check
            destNeighborhood.removeNeighbor(srcId);
            destRecord.adjacencySetSize--;
        }

        // Check for conversion to PowerOfTwo
        if (srcRecord.adjacencySetSize < BLOCK_SIZE) {
            convertToPowerofTwo(srcRecord);
        }
        if (destRecord.adjacencySetSize < BLOCK_SIZE) {
            convertToPowerofTwo(destRecord);
        }

        assert wellFormed() : "Invariant failed at end of deleteEdge.";
    }

    /**
     * Checks if a vertex with a given logical ID exists in the graph.
     * 
     * @param v the logical ID of the vertex to check.
     * @return true if the vertex exists, otherwise: false.
     */
    public boolean hasVertex(int v) {
        return logicalToPhysical.containsKey(v);	// Uses containsKey() from the Map interface
    }

    /**
     * Inserts a new vertex in the graph.
     *
     * @param id The vertex ID to insert.
     * @throws IllegalArgumentException if the vertex ID is null.
     * @throws IllegalStateException    if the vertex already exists.
     */
    public void insertVertex(T id) { 
        if (id == null) {
            throw new IllegalArgumentException("@insertVertex, the parameter, id, may not be null.");
        }

        int logicalID = id.hashCode();
        if (logicalToPhysical.containsKey(logicalID)) {
            throw new IllegalStateException("Vertex already exists: " + id);
        }

        assert wellFormed() : "Invariant failed at start of insertVertex.";

        int physicalIndex = vertexCount;

        ensureCapacity(physicalIndex + 1);

        // Place the new Vertex in the lp-index and pl-index
        logicalToPhysical.put(logicalID, physicalIndex);
        physicalToLogical[physicalIndex] = logicalID;

        // Create the vertex record in the adjacency index
        Neighborhood<T> neighborhood = new PowerofTwo<>();
        VertexRecord<T> entry = new VertexRecord<>(logicalID, neighborhood);
        adjacencyIndex[physicalIndex] = entry; 

        vertexCount++;

        assert wellFormed() : "Invariant failed at end of insertVertex.";
    }

    /**
     * Deletes a vertex and all its associated edges from the graph.
     *
     * @param id The vertex ID to remove.
     * @throws IllegalArgumentException if the vertex ID is null or does not exist.
     */
    public void deleteVertex(T id) {
        if (id == null) {
            throw new IllegalArgumentException("@deleteVertex, the parameter, id, may not be null.");
        }
        assert wellFormed() : "Invariant failed at start of deleteVertex.";

        // Retrieve the logical and physical index for the vertex
        int logicalID = id.hashCode();
        Integer physicalIndexObj = logicalToPhysical.get(logicalID);
        if (physicalIndexObj == null) {
            throw new IllegalArgumentException("The vertex to delete does not exist in the graph.");
        }
        int physicalIndex = physicalIndexObj;

        // Retrieve the vertex record
        VertexRecord<T> vertexRecord = adjacencyIndex[physicalIndex];

        // Remove all edges associated with the vertex
        List<T> neighbors = new ArrayList<>(vertexRecord.adjacencySet.getNeighbors()); // To avoid ConcurrentModificationException
        for (T neighbor : neighbors) {
            deleteEdge(id, neighbor); // Handles adjacency set updates
        }

        // Remove the vertex from mappings
        logicalToPhysical.remove(logicalID);
        adjacencyIndex[physicalIndex] = null;
        physicalToLogical[physicalIndex] = null;

        // Swap the last vertex into the deleted slot if it's not the last one
        int lastPhysicalIndex = vertexCount - 1;
        if (physicalIndex != lastPhysicalIndex) {
            // Retrieve the logical ID of the last vertex
            Integer lastLogicalID = physicalToLogical[lastPhysicalIndex];
            if (lastLogicalID == null) {
                throw new IllegalStateException("Last physical index does not have a logical ID.");
            }

            // Swap in the adjacency index
            adjacencyIndex[physicalIndex] = adjacencyIndex[lastPhysicalIndex];
            adjacencyIndex[lastPhysicalIndex] = null;

            // Update the physicalToLogical mapping
            physicalToLogical[physicalIndex] = lastLogicalID;
            physicalToLogical[lastPhysicalIndex] = null;

            // Update the logicalToPhysical mapping for the moved vertex
            logicalToPhysical.put(lastLogicalID, physicalIndex);
        }

        // Decrement the vertex count
        vertexCount--;

        assert wellFormed() : "Invariant failed at end of deleteVertex.";
    }

    /**
     * Checks if an edge exists between two vertices.
     *
     * @param srcId  The source vertex ID.
     * @param destId The destination vertex ID.
     * @return True if the edge exists, otherwise false.
     * @throws IllegalArgumentException if the srcID is null.
     */
    public boolean findEdge(T srcId, T destId) {
        if (srcId == null) {
            throw new IllegalArgumentException("@findEdge, the parameter, srcId, may not be null.");
        }
        assert wellFormed() : "Invariant failed at start of findEdge.";

        // Check if the source vertex exists
        Integer srcPhysicalId = logicalToPhysical.get(srcId.hashCode());
        if (srcPhysicalId == null) {
            return false; // Source vertex does not exist
        }

        // Retrieve the source vertex's record
        VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];

        // Check if the destination vertex exists in the source's neighborhood
        boolean edgeExists = srcRecord.adjacencySet.contains(destId); // Efficient check

        assert wellFormed() : "Invariant failed at end of findEdge.";

        return edgeExists;
    }

    /**
     * Processes all neighbors of a given vertex using the provided action.
     *
     * @param vertexId The ID of the vertex whose neighbors are to be scanned.
     * @param action   The action to perform on each neighbor.
     * @throws IllegalArgumentException if the vertex does not exist.
     */
    public void scanNeighbors(T vertexId, Consumer<T> action) {
        if (vertexId == null) {
            throw new IllegalArgumentException("vertexId cannot be null.");
        }
        assert wellFormed() : "Invariant failed at start of scanNeighbors.";

        // Retrieve the physical ID for the vertex
        Integer physicalId = logicalToPhysical.get(vertexId.hashCode());
        if (physicalId == null) {
            throw new IllegalArgumentException("Vertex does not exist: " + vertexId);
        }

        // Retrieve the adjacency set
        VertexRecord<T> vertexRecord = adjacencyIndex[physicalId];

        // Process each neighbor
        for (T neighbor : vertexRecord.adjacencySet.getNeighbors()) {
            action.accept(neighbor);
        }

        assert wellFormed() : "Invariant failed at end of scanNeighbors.";
    }

    /**
     * Finds the intersection of neighbors between two vertices.
     *
     * @param v1Id The first vertex ID.
     * @param v2Id The second vertex ID.
     * @return A list of IDs that represent the common neighbors.
     * @throws IllegalArgumentException if either vertex ID is null or if one of the vertices does not exist.
     */
    public List<T> intersectNeighbors(T v1Id, T v2Id) {
        if (v1Id == null || v2Id == null) {
            throw new IllegalArgumentException("Vertex IDs cannot be null.");
        }

        // Retrieve physical IDs for both vertices
        Integer v1PhysicalId = logicalToPhysical.get(v1Id.hashCode());
        Integer v2PhysicalId = logicalToPhysical.get(v2Id.hashCode());

        if (v1PhysicalId == null || v2PhysicalId == null) {
            throw new IllegalArgumentException("One or both vertices do not exist in the graph.");
        }

        // Retrieve the neighborhoods of both vertices
        Neighborhood<T> v1Neighborhood = adjacencyIndex[v1PhysicalId].adjacencySet;
        Neighborhood<T> v2Neighborhood = adjacencyIndex[v2PhysicalId].adjacencySet;

        return v1Neighborhood.intersect(v2Neighborhood);
    }

    /**
     * Retrieves the physical ID corresponding to the given logical vertex ID.
     *
     * This method returns the physical ID of a vertex, if it exists, in the logical-to-physical mapping.
     * If the vertex does not exist, this will return null.
     *
     * @param logicalID The logical ID of the vertex.
     * @return The physical ID of the vertex, or null if the vertex is not present.
     */
    public Integer physicalId(int logicalID) {
        return logicalToPhysical.get(logicalID);
    }

    /**
     * Retrieves the logical ID corresponding to the given physical vertex ID.
     *
     * This method returns the logical ID of a vertex, if it exists, in the physical-to-logical mapping.
     * If the vertex does not exist, this will return null.
     *
     * @param physicalID The physical ID of the vertex.
     * @return The logical ID of the vertex, or null if the vertex is not present.
     * @throws IllegalArgumentException if the physical ID is out of bounds.
     */
    public Integer logicalId(int physicalID) {
        if (physicalID < 0 || physicalID >= vertexCount) {
            throw new IllegalArgumentException("Physical ID out of bounds: " + physicalID);
        }
        return physicalToLogical[physicalID];
    }

    /**
     * Changes the current capacity of the pl-index and adjacency index, if needed.
     *
     * @param minimumCapacity the new capacity for these fields
     * @postcondition The capacities have been changed to at least minimumCapacity.
     *                If the capacity was already at or greater than minimumCapacity,
     *                then the capacity is left unchanged.
     *                If the capacity is changed, it must be at least twice as big as before.
     * @exception OutOfMemoryError Indicates insufficient memory for: new array of minimumCapacity elements.
     **/
    @SuppressWarnings("unchecked")
    private void ensureCapacity(int minimumCapacity) {
        if (adjacencyIndex.length < minimumCapacity) {
            // Determine the new length (at least twice as big as before)
            int newLength = adjacencyIndex.length * 2;
            if (minimumCapacity > newLength) {
                newLength = minimumCapacity;
            }

            // Resize the adjacencyIndex array
            VertexRecord<T>[] newAdjacencyIndex = (VertexRecord<T>[]) new VertexRecord[newLength];
            System.arraycopy(adjacencyIndex, 0, newAdjacencyIndex, 0, adjacencyIndex.length);
            adjacencyIndex = newAdjacencyIndex;

            // Resize the physicalToLogical array
            Integer[] newPhysicalToLogical = new Integer[newLength];
            System.arraycopy(physicalToLogical, 0, newPhysicalToLogical, 0, physicalToLogical.length);
            physicalToLogical = newPhysicalToLogical;
        }
    }

    /**
     * Converts the Neighborhood of a given vertex from PowerOfTwo to an UnrolledSkipList if 
     * its size exceeds the threshold, BLOCK_SIZE (as checked and called elsewhere).
     *
     * @param physicalIndex The physical index of the vertex in the adjacency index.
     */
    private void convertToUnrolledSkipList(int physicalIndex) {
        VertexRecord<T> vertexRecord = adjacencyIndex[physicalIndex];
        Neighborhood<T> currentNeighborhood = vertexRecord.adjacencySet;

        // If already an UnrolledSkipList, no conversion needed
        if (currentNeighborhood instanceof UnrolledSkipList) {
            return;
        }

        // Create a new UnrolledSkipList and transfer neighbors
        UnrolledSkipList<T> newNeighborhood = new UnrolledSkipList<>();
        for (T neighbor : currentNeighborhood.getNeighbors()) {
            newNeighborhood.addNeighbor(neighbor);
        }

        vertexRecord.adjacencySet = newNeighborhood;
    }

    /**
     * Converts the Neighborhood of a given vertex to a PowerOfTwo if its size falls below 
     * the threshold, BLOCK_SIZE (as checked and called in deleteEdge)
     *
     * @param vertexRecord The VertexRecord of the vertex in the adjacency index.
     */
    private void convertToPowerofTwo(VertexRecord<T> vertexRecord) {
        Neighborhood<T> currentNeighborhood = vertexRecord.adjacencySet;

        // If already a PowerOfTwo, no conversion needed
        if (currentNeighborhood instanceof PowerofTwo) {
            return;
        }

        // Create a new PowerofTwo adjacency set and transfer neighbors
        PowerofTwo<T> newNeighborhood = new PowerofTwo<>();
        for (T neighbor : currentNeighborhood.getNeighbors()) {
            newNeighborhood.addNeighbor(neighbor);
        }

        vertexRecord.adjacencySet = newNeighborhood;
    }

    /**
     * Spy class for testing purposes.
     */
    public static class Spy {
        /**
         * Return the sink for invariant error messages.
         * 
         * @return current reporter.
         */
        public Consumer<String> getReporter() {
            return reporter;
        }

        /**
         * Change the sink for invariant error messages.
         * 
         * @param r where to send invariant error messages.
         */
        public void setReporter(Consumer<String> r) {
            reporter = r;
        }

        /**
         * Create a debugging instance of the SortledtonGraph with a particular data structure.
         * 
         * @param vertexCount       the vertex count.
         * @param logicalToPhysical the logicalToPhysical map.
         * @param adjacencyIndex    the adjacency index array.
         * @return a new instance of a SortledtonGraph with the given data structure.
         */
        @SuppressWarnings("unchecked")
        public static <U extends Comparable<U>> SortledtonGraph<U> newInstance(int vertexCount,
                Map<Integer, Integer> logicalToPhysical, VertexRecord<U>[] adjacencyIndex) {
            SortledtonGraph<U> result = new SortledtonGraph<>();
            result.vertexCount = vertexCount;
            result.logicalToPhysical = new HashMap<>(logicalToPhysical);

            // Clone the adjacencyIndex array
            VertexRecord<U>[] newIndex = (VertexRecord<U>[]) Array.newInstance(VertexRecord.class, adjacencyIndex.length);
            for (int i = 0; i < adjacencyIndex.length; i++) {
                if (adjacencyIndex[i] != null) {
                    // Deep copy if necessary
                    // Assuming VertexRecord has a proper copy constructor or clone method
                    Neighborhood<U> clonedNeighborhood;
                    if (adjacencyIndex[i].adjacencySet instanceof PowerofTwo) {
                        clonedNeighborhood = new PowerofTwo<>();
                        for (U neighbor : adjacencyIndex[i].adjacencySet.getNeighbors()) {
                            clonedNeighborhood.addNeighbor(neighbor);
                        }
                    } else if (adjacencyIndex[i].adjacencySet instanceof UnrolledSkipList) {
                        clonedNeighborhood = new UnrolledSkipList<>();
                        for (U neighbor : adjacencyIndex[i].adjacencySet.getNeighbors()) {
                            clonedNeighborhood.addNeighbor(neighbor);
                        }
                    } else {
                        throw new IllegalStateException("Unknown Neighborhood implementation.");
                    }
                    newIndex[i] = new VertexRecord<>(adjacencyIndex[i].logicalId, clonedNeighborhood);
                    newIndex[i].adjacencySetSize = adjacencyIndex[i].adjacencySetSize;
                }
            }
            result.adjacencyIndex = newIndex;

            // Reconstruct the physicalToLogical array
            Integer[] newPhysicalToLogical = new Integer[newIndex.length];
            for (Map.Entry<Integer, Integer> entry : logicalToPhysical.entrySet()) {
                int logicalId = entry.getKey();
                int physicalIndex = entry.getValue();
                newPhysicalToLogical[physicalIndex] = logicalId;
            }
            result.physicalToLogical = newPhysicalToLogical;

            return result;
        }

        /**
         * Return whether the debugging instance meets the requirements on the invariant.
         * 
         * @param sg instance of SortledtonGraph to use, must not be null.
         * @return whether it passes the check.
         */
        public static boolean wellFormed(SortledtonGraph<?> sg) {
            return sg.wellFormed();
        }
    }
}
