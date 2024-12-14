package edu.uwm.cs351;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.lang.reflect.Array;
import java.util.HashMap;

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
 * 	<li>Sortledton C++ implementation by Per Fuchs et al: 
 * 	<a href= "https://gitlab.db.in.tum.de/per.fuchs/sortledton"> Sortledton GitLab repository</a></li>
 *</ul>
 *
 * @param <T> The type of the vertex ID
 */
public class SortledtonGraph<T extends Comparable<T>> {
	//Constants
	private static final int INITIAL_VECTOR_SIZE = 131072;	// Note: Can update this later, as needed. This based is based on the authors' 
															// implementation that we referenced.
	private static final int BLOCK_SIZE = 128; 				// "block size", as described in section 4.2 - Data Structure. 
															// This is the suggested threshold to switch between power of two vectors and 
															// unrolled skip lists for the adjacency sets ("Neighborhoods")
	
	//Fields
	private int vertexCount = 0;
	private HashMap<Integer, Integer> logicalToPhysical;	// "lp-index" from Figure 6 - Maps hash codes to pl indices
	private Integer[] physicalToLogical;					// "pl-index" - values are hash codes of the neighborhoods, indices are 1:1 to adjacencyIndex
	private VertexRecord<T>[] adjacencyIndex;				// Adjacency Index. indices are 1:1 to pl-index
	
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
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
		//1. the lp-index and pl index must not be null
		if (logicalToPhysical == null || physicalToLogical == null || adjacencyIndex == null) return report("Data structures for graph must not be null.");
		
		//2. the neighborhood for each used hashCode key must not be null
		for (Entry<Integer, Integer> entry : logicalToPhysical.entrySet()) {
			Integer logicalID = entry.getKey();
			Integer physicalIndex = entry.getValue();

			if (physicalIndex == null) return report("physical index is undefined for logical ID: " + logicalID);
			
			//The pl-index must also map correctly
			if (physicalToLogical[physicalIndex] != logicalID) return report("the lp and pl indices do not correctly reference each other for " + logicalID);
		}
		
		//Check all entries in the index array 
		for (int i = 0; i < adjacencyIndex.length; i++) {
			VertexRecord<T> ve = adjacencyIndex[i];
			if (ve != null) {
				
				//3. Check that adjacency set size is non-negative
				if (ve.adjacencySetSize < 0) return report("Negative adjacency set size at index " + i);

				//4. Verify the ID mapping is consistent with the lp-index
				if (!logicalToPhysical.containsKey(ve.logicalId)) return report("Logical ID " + ve.logicalId + " in VertexRecord is not in logicalToPhysical.");
			}
		}			

	    //5. Check that vertex count matches the number of entries in logicalToPhysical
	    if (vertexCount != logicalToPhysical.size()) return report("Vertex count does not match the number of entries in logicalToPhysical.");

		//Passes all the checks
		return true;
	}
	
	
	/**
	 * Constructs a new SortledtonGraph with an empty adjacency index.
	 */
	@SuppressWarnings("unchecked")
	public SortledtonGraph() {
		adjacencyIndex = (VertexRecord<T>[]) new VertexRecord[INITIAL_VECTOR_SIZE];
		physicalToLogical = new Integer[INITIAL_VECTOR_SIZE];
		logicalToPhysical = new HashMap<>(INITIAL_VECTOR_SIZE);
		assert wellFormed() : "invariant failed at end of SortledtonGraph constructor";
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
	 * @return 	A list of IDs representing the neighbors of the specified vertex,
	 * 			or an empty list if the vertex has no neighbors or does not exist.
	 * @throws IllegalArgumentException if the vertex ID is null.
	 */
	public List<T> getNeighbors(T vertexId) {
		Integer physicalID = logicalToPhysical.get(vertexId.hashCode());
		VertexRecord<T> assocVR = adjacencyIndex[physicalID];
		Neighborhood<T> neighborhood = assocVR.adjacencySet;
		if (neighborhood == null) {
			throw new IllegalArgumentException("Vertex does not exist: " + vertexId);
		}
		return neighborhood.getNeighbors();
	}

	/**
	 * Inserts an edge between two vertices. Creates vertices automatically if they don’t already exist.
	 *
	 * @param srcId The source vertex ID.
	 * @param destId The destination vertex ID.
	 * @throws IllegalArgumentException if either ID is null
	 */
	public void insertEdge(T srcId, T destId) { 
		if (srcId == null || destId == null) throw new IllegalArgumentException("@insertEdge, the parameters, srcID and destID may not be null.");
		assert wellFormed() : "invariant failed at start of insertEdge";
		
		// Ensure both vertices exist
		Integer srcLogicalId = srcId.hashCode();
		Integer destLogicalId = destId.hashCode();
	    if (!logicalToPhysical.containsKey(srcLogicalId)) insertVertex(srcId);
	    if (!logicalToPhysical.containsKey(destLogicalId)) insertVertex(destId);
	    
	    // Retrieve physical IDs for both vertices
	    int srcPhysicalId = logicalToPhysical.get(srcLogicalId);
	    int destPhysicalId = logicalToPhysical.get(destLogicalId);

	    // Update adjacencyIndex for srcId
	    VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];
	    if (!srcRecord.adjacencySet.getNeighbors().contains(destId)) { 	// only make the change if the edge does not already exist
	        srcRecord.adjacencySet.addNeighbor(destId);
	        srcRecord.adjacencySetSize++;
	    }

	    // Update adjacencyIndex for destId
	    VertexRecord<T> destRecord = adjacencyIndex[destPhysicalId];
	    if (!destRecord.adjacencySet.getNeighbors().contains(srcId)) {	// only make the change if the edge does not already exist
	        destRecord.adjacencySet.addNeighbor(srcId);
	        destRecord.adjacencySetSize++;
	    }
	    
	    // Check for conversion to UnrolledSkipList
	    if (srcRecord.adjacencySetSize >= BLOCK_SIZE) convertToUnrolledSkipList(srcPhysicalId);
	    if (destRecord.adjacencySetSize >= BLOCK_SIZE) convertToUnrolledSkipList(destPhysicalId);
	    	    
	    assert wellFormed() : "invariant failed at end of insertEdge";
	}

	/**
	 * Deletes an edge between two vertices if it exists.
	 *
	 * @param srcId The source vertex ID.
	 * @param destId The destination vertex ID.
	 * @throws IllegalArgumentException if either ID is null
	 */
	public void deleteEdge(T srcId, T destId) { 
		if (srcId == null || destId == null) throw new IllegalArgumentException("@deleteEdge, the parameters, srcID and destID may not be null.");
		
		assert wellFormed() : "invariant failed at start of deleteEdge";
		
		// Ensure both vertices exist
	    Integer srcPhysicalId = logicalToPhysical.get(srcId.hashCode());
	    Integer destPhysicalId = logicalToPhysical.get(destId.hashCode());

	    if (srcPhysicalId == null || destPhysicalId == null) throw new IllegalArgumentException("One or both vertices do not exist in the current state.");
	    	
	    // Retrieve the vertex records
	    VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];
	    VertexRecord<T> destRecord = adjacencyIndex[destPhysicalId];
	    
	    // Remove the destination vertex from the source vertex's neighborhood
        Neighborhood<T> srcNeighborhood = srcRecord.adjacencySet;
        srcNeighborhood.removeNeighbor(destId);
        srcRecord.adjacencySetSize--;
        
        // Remove the source vertex from the destination vertex's neighborhood
        Neighborhood<T> destNeighborhood = destRecord.adjacencySet;
        destNeighborhood.removeNeighbor(srcId);
        destRecord.adjacencySetSize--;
        
        // Check for conversion to PowerofTwo
        if (destRecord.adjacencySetSize < BLOCK_SIZE) convertToPowerofTwo(destRecord);
                
		assert wellFormed() : "invariant failed at end of deleteEdge";
	}
	
	/**
	 * Checks if a vertex with a given logical ID exists in the graph.
	 * 
	 * @param v the logical ID of the vertex to check.
	 * @return true if the vertex exists, otherwise: false.
	 */
	public boolean hasVertex(int v) {
        return logicalToPhysical.containsKey(v);	//used containsKey( ) from the Map interface
    }
	
	/**
	 * Inserts a new vertex in the graph.
	 *
	 * @param id The vertex ID to insert.
	 * @throws IllegalArgumentException if the vertex ID is null.
	 * @throws IllegalStateException if the vertex already exists.
	 */
	public void insertVertex(T id) { 
		if (id == null) throw new IllegalArgumentException("@insertVertex, the parameter, id, may not be null.");
		
		int logicalID = id.hashCode();
		if (logicalToPhysical.containsKey(logicalID)) throw new IllegalStateException("Vertex already exits: " + id);
		
		assert wellFormed() : "invariant failed at start of insertVertex.";
		
		int physicalIndex = vertexCount;
		
		ensureCapacity(physicalToLogical.length + 1);
		
		// place the new Vertex in the lp-index and pl-index
		logicalToPhysical.put(logicalID, physicalIndex);
		physicalToLogical[physicalIndex] = logicalID;
		
		// create the vertex record in the adjacency index
		Neighborhood<T> neighborhood = new PowerofTwo<>();
		VertexRecord<T> entry = new VertexRecord<>(logicalID, neighborhood);
	    adjacencyIndex[physicalIndex] = entry; 
		
	    vertexCount++;
		
	    assert wellFormed() : "invariant failed at end of insertVertex.";
	}

	/**
	 * Deletes a vertex and all its associated edges from the graph.
	 *
	 * @param id The vertex ID to remove.
	 * @throws IllegalArgumentException if the vertex ID is null.
	 */
	public void deleteVertex(T id) {
		if (id == null) throw new IllegalArgumentException("@deleteVertex, the parameter, id, may not be null.");
		assert wellFormed() : "invariant failed at start of deleteVertex.";

		// Retrieve the physical index for the vertex and its vertex record from the adj-index
		Integer logicalIndex = id.hashCode();
	    Integer physicalIndex = logicalToPhysical.get(logicalIndex);
	    if (physicalIndex == null) throw new IllegalArgumentException("The vertex to delete does not exist in the graph.");
	    VertexRecord<T> vertexRecord = adjacencyIndex[physicalIndex];

	    // Remove all edges associated with the vertex in other vertices' neighborhood objects.
	    List<T> neighbors = vertexRecord.adjacencySet.getNeighbors();
	    // TODO this was a simple way to implement this, but does this assume an undirected graph? maybe we need a check here to improve efficiency
	    for (T neighbor : neighbors) deleteEdge(id, neighbor);		// Note: potential PowerofTwo conversion handled by deleteEdge
	    
	    // Remove the vertex from lp-index, pl-index, and the adj-index
	    logicalToPhysical.remove(logicalIndex);
	    physicalToLogical[physicalIndex] = null;
	    adjacencyIndex[physicalIndex] = null;

	    // Decrement the vertex count
	    vertexCount--;
		
		assert wellFormed() : "invariant failed at end of deleteVertex."; 
	}

	/**
	 * Checks if an edge exists between two vertices.
	 *
	 * @param srcId The source vertex ID.
	 * @param destId The destination vertex ID.
	 * @return True if the edge exists, otherwise false.
	 * @throws IllegalArgumentException if the srcID is null.
	 */
	public boolean findEdge(T srcId, T destId) {
		if (srcId == null) throw new IllegalArgumentException("@findEdge, the parameter, srcId, may not be null.");
	    assert wellFormed() : "invariant failed at start of findEdge";

	    // Check if the source vertex exists
	    Integer srcPhysicalId = logicalToPhysical.get(srcId.hashCode());
	    if (srcPhysicalId == null) return false; // Source vertex does not exist
	    
	    // Retrieve the source vertex's record
	    VertexRecord<T> srcRecord = adjacencyIndex[srcPhysicalId];

	    // Check if the destination vertex exists in the source's neighborhood
	    boolean edgeExists = srcRecord.adjacencySet.getNeighbors().contains(destId);

	    assert wellFormed() : "invariant failed at end of findEdge";

	    return edgeExists;
	}

	/**
	 * Processes all neighbors of a given vertex using the provided (?) action.
	 * TODO [I'm not clear what the function of this method is intended to be.
	 *       I've added it with a Consumer to be sort of generic. Check Per Fuchs
	 *       implementation for more details. -Dustin]
	 *
	 * @param vertexId The ID of the vertex whose neighbors are to be scanned.
	 * @param action The action to perform on each neighbor.
	 * @throws IllegalArgumentException if the vertex does not exist.
	 */
	public void scanNeighbors(T vertexId, Consumer<T> action) {
	    if (vertexId == null) throw new IllegalArgumentException("vertexId cannot be null.");
	    assert wellFormed() : "invariant failed at start of scanNeighbors";
	    
	    // Retrieve the physical ID for the vertex
	    Integer physicalId = logicalToPhysical.get(vertexId.hashCode());
	    if (physicalId == null) throw new IllegalArgumentException("Vertex does not exist: " + vertexId);
	    
	    // Retrieve the adjacency set
	    VertexRecord<T> vertexRecord = adjacencyIndex[physicalId];

	    // Process each neighbor
	    for (T neighbor : vertexRecord.adjacencySet.getNeighbors()) {
	        action.accept(neighbor);
	    }
	    
	    assert wellFormed() : "invariant failed at end of scanNeighbors";
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
		if (v1Id == null || v2Id == null) throw new IllegalArgumentException("Vertex IDs cannot be null.");

		// Retrieve physical IDs for both vertices
		Integer v1PhysicalId = logicalToPhysical.get(v1Id.hashCode());
		Integer v2PhysicalId = logicalToPhysical.get(v2Id.hashCode());

		if (v1PhysicalId == null || v2PhysicalId == null) throw new IllegalArgumentException("One or both vertices do not exist in the graph.");
		
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
     * @param v The logical ID of the vertex.
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
     * @param v The physical ID of the vertex.
     * @return The logical ID of the vertex, or null if the vertex is not present.
     */
	public int logicalId(int v) {
		return adjacencyIndex[v].getLogicalId();
	}
	
	/**
	 * Change the current capacity of the pl-index and adjacency index, if needed.
	 *
	 * @param minimumCapacity
	 *   the new capacity for these fields
	 * @postcondition
	 *   The capacities have been changed to at least minimumCapacity.
	 *   If the capacity was already at or greater than minimumCapacity,
	 *   then the capacity is left unchanged.
	 *   If the capacity is changed, it must be at least twice as big as before.
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for: new array of minimumCapacity elements.
	 **/
	private void ensureCapacity(int minimumCapacity) {
	    if (adjacencyIndex.length < minimumCapacity) {
	        // Determine the new length (at least twice as big as before)
	        int newLength = adjacencyIndex.length * 2;
	        if (minimumCapacity > newLength) {
	            newLength = minimumCapacity;
	        }

	        // Resize the adjacencyIndex array
	        @SuppressWarnings("unchecked")
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

	    // If already an UnrolledSkipList, no conversion needed - this should never run 
	    // but is included due to the time efficiency cost of this method.
	    if (currentNeighborhood instanceof UnrolledSkipList) return;
	    
	    // Create a new UnrolledSkipList and transfer neighbors
	    UnrolledSkipList<T> newNeighborhood = new UnrolledSkipList<>();
	    for (T neighbor : currentNeighborhood.getNeighbors()) {
	        newNeighborhood.addNeighbor(neighbor);
	    }

	    vertexRecord.adjacencySet = newNeighborhood;
	}

	/**
	 * Converts the Neighborhood of a given vertex to a PowerofTwo if its size falls below 
	 * the threshold, BLOCK_SIZE (as checked and called in deleteEdge)
	 *
	 * @param physicalIndex The physical index of the vertex in the adjacency index.
	 */
	private void convertToPowerofTwo(VertexRecord<T> vertexRecord) {
	    Neighborhood<T> currentNeighborhood = vertexRecord.adjacencySet;

	    // If already a PowerofTwo, no conversion needed - this should never run 
	    // but is included due to the time efficiency cost of this method.
	    if (currentNeighborhood instanceof PowerofTwo) return;
	    
	    // Create a new PowerofTwo adjacency set and transfer neighbors/edges
	    PowerofTwo<T> newNeighborhood = new PowerofTwo<>();
	    for (T neighbor : currentNeighborhood.getNeighbors()) {
	        newNeighborhood.addNeighbor(neighbor);
	    }

	    vertexRecord.adjacencySet = newNeighborhood;
	}

	private void insertEdgeByPhysicalId(int srcPhysicalId, int destPhysicalId) {
		//***TODO***//
	}
	private void deleteEdgeByPhysicalId(int srcPhysicalId, int destPhysicalId) {
		//***TODO***//
	}
	private void deleteVertexByPhysicalId(int physicalId) {
		//***TODO***//
	}
	private List<T> getNeighborsByPhysicalId(int physicalId) {
		//***TODO***//
		return null;
	}
	private boolean findEdgeByPhysicalId(int srcPhysicalId, int destPhysicalId) {
		//***TODO***//
		return true;
	}
	private List<T> intersectNeighborsByPhysicalId(int v1PhysicalId, int v2PhysicalId) {
		//***TODO***//
		return null;
	}

	
    public static class Spy {
        /**
         * Return the sink for invariant error messages.
         * @return current reporter.
         */
        public Consumer<String> getReporter() {
            return reporter;
        }

        /**
         * Change the sink for invariant error messages.
         * @param r where to send invariant error messages.
         */
        public void setReporter(Consumer<String> r) {
            reporter = r;
        }

        /**
         * Create a debugging instance of the SortledtonGraph with a particular data structure.
         * @param vertexCount the vertex count.
         * @param logicalToPhysical the logicalToPhysical map.
         * @param index the index array.
         * @return a new instance of a SortledtonGraph with the given data structure.
         */
        public static <U extends Comparable<U>> SortledtonGraph<U> newInstance(int vertexCount, Map<Integer, Integer> logicalToPhysical, VertexRecord<U>[] adjacencyIndex) {
            SortledtonGraph<U> result = new SortledtonGraph<>();
            result.vertexCount = vertexCount;
            result.logicalToPhysical = new HashMap<Integer, Integer>(logicalToPhysical);
			
            // Clone the index array and assign it to the result.
			
			@SuppressWarnings("unchecked") // We know that it's safe to cast as VertexRecord<Integer>[] here because we're constructing the array directly
			VertexRecord<U>[] newIndex = (VertexRecord<U>[]) Array.newInstance(VertexRecord.class, adjacencyIndex.length);

			for (int i = 0; i < adjacencyIndex.length; i++) {
				
				if (adjacencyIndex[i] != null) {
					// TODO: Should this be constructing NEW instances, rather than assigning existing ones?
					result.adjacencyIndex[i] = adjacencyIndex[i];
				}
			}

			result.adjacencyIndex = newIndex;

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
         * @param sg instance of SortledtonGraph to use, must not be null.
         * @return whether it passes the check.
         */
        public static boolean wellFormed(SortledtonGraph<?> sg) {
            return sg.wellFormed();
        }
    }
	
}
