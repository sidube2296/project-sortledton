package edu.uwm.cs351;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.ArrayList;
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
	private static final int INITIAL_VECTOR_SIZE = 128; //TODO update this later when we start to scale. Per Fuchs used 131072
	
	//Fields
	private int vertexCount = 0;
	private HashMap<Integer, Neighborhood<T>> logicalToPhysical = new HashMap<>(INITIAL_VECTOR_SIZE);	//"lp-index" from Figure 6
	@SuppressWarnings("unchecked")
	private VertexEntry[] index = new VertexEntry[INITIAL_VECTOR_SIZE]; //Eclipse gives an error, but this should be a safe cast.						//Adjacency Index. Contains fields logicalID --> "pl-index" & adjacencySet --> "adj. set pointer"
	
	// A map to store each vertex and its corresponding VertexRecord */
	//TODO is this not needed?
	//private final Map<T, VertexRecord<T>> adjacencyIndex;
	
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
		//TODO
		if (logicalToPhysical == null || index == null) {
	        reporter.accept("Data structures for graph must not be null.");
	        return false;
	    }

	    // Check that logicalToPhysical has valid mappings within the index array bounds
	    for (Map.Entry<T, Integer> entry : logicalToPhysical.entrySet()) {
	        int logicalID = entry.getKey(); //TODO sort this out. The Map should be <T, Integer> or vice versa? If not change, change VertexEntry to suit
	        int physicalIndex = entry.getValue();
	        if (physicalIndex < 0 || physicalIndex >= index.length) {
	            reporter.accept("Physical index out of bounds for logical ID " + logicalID);
	            return false;
	        }

	        VertexEntry ve = index[physicalIndex];
	        if (ve == null) {
	            reporter.accept("VertexEntry at physical index " + physicalIndex + " is null.");
	            return false;
	        }

	        if (ve.logicalId != logicalID) {
	            reporter.accept("Logical ID mismatch at physical index " + physicalIndex);
	            return false;
	        }
	    }

	    // Check all entries in the index array
	    for (int i = 0; i < index.length; i++) {
	        VertexEntry ve = index[i];
	        if (ve != null) {
	            if (ve.adjacencySetSize < 0) {
	                reporter.accept("Negative adjacency set size at index " + i);
	                return false;
	            }
	        }
	    }

	    // Check that vertex count matches the number of entries in logicalToPhysical
	    if (vertexCount != logicalToPhysical.size()) {
	        reporter.accept("Vertex count does not match the number of entries in logicalToPhysical.");
	        return false;
	    }
		//Passes all the checks
		return true;
	}
	
	
	/**
	 * Constructs a new SortledtonGraph with an empty adjacency index.
	 */
	public SortledtonGraph() {
		for (int i = 0; i < INITIAL_VECTOR_SIZE; i++) {
			index[i] = new VertexEntry();
		}
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
		if (vertexId == null) {
			throw new IllegalArgumentException("Vertex ID cannot be null.");
		}

		// Retrieve the physical index corresponding to the logical vertex ID
		Integer physicalId = logicalToPhysical.get(vertexId); // TODO Assuming vertexId.hashCode() maps to logicalId
		if (physicalId == null) {
			return new ArrayList<T>(); // Vertex does not exist, return an empty list
		}

		// Access the VertexEntry and retrieve its neighborhood
		VertexEntry entry = index[physicalId];
		return entry.adjacencySet.getNeighbors();
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
		/*...*/ 
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
		/*...*/ 
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
		if (logicalToPhysical.containsKey(id.hashCode())) throw new IllegalStateException("Vertex already exits: " + id);
		assert wellFormed() : "invariant failed at start of insertVertex.";
		logicalToPhysical.put(id.hashCode(), new PowerofTwo<>());
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
		if (id == null) throw new IllegalArgumentException("@insertVertex, the parameter, id, may not be null.");
		assert wellFormed() : "invariant failed at start of deleteVertex.";
		/*...*/ 
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
		/*...*/ 
		return true;
	}

	/**
	 * Finds the intersection of neighbors between two vertices.
	 *
	 * @param v1Id The first vertex ID.
	 * @param v2Id The second vertex ID.
	 * @return A list of IDs that represent the common neighbors.
	 */
	public List<T> intersectNeighbors(T v1Id, T v2Id) {
		/*...*/
		return null;
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
	public int physicalId(int v) {
		return logicalToPhysical.get(v);
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
		return index[v].getLogicalId();
	}
	
	 /**
     * An entry in the vertex index that stores information about a vertex.
     */
    private class VertexEntry {
        public Neighborhood<T> adjacencySet;
        public int logicalId;
        public int adjacencySetSize;

    	/**
    	 * Checks that the VertexEntry invariant is correctly adhered to.
    	 * 
    	 * @return true when in compliance with all listed invariants
    	 */
    	private boolean wellFormed() {
    		//TODO
    		return true;
    	}
        
        /**
         * Initializes a new VertexEntry, with adjacency set size set to zero.
         * The adjacency set represents the number of neighbors connected to this vertex.
         */
        public VertexEntry() {
            adjacencySetSize = 0;
            adjacencySet = new PowerofTwo<T>(); // Default to PowerofTwo for small neighborhoods
    		assert wellFormed() : "invariant failed at end of VertexEntry constructor.";
        }

        /**
         * Gets the size of the adjacency set for this vertex.
         *
         * @return the number of neighbors for this vertex.
         */
        public int getAdjacencySetSize() {
            return adjacencySetSize;
        }
        
        /**
         * Gets the logical ID associated with this vertex.
         *
         * @return the logical ID of the vertex
         */
        public int getLogicalId() {
            return logicalId;
        }
        
        /**
         * Setter for the logical ID for this vertex.
         *
         * @param logicalId the logical ID to set
         */
        public void setLogicalId(int logicalId) {
    		assert wellFormed() : "invariant failed at start of setLogicalId.";
    		this.logicalId = logicalId; 
    		assert wellFormed() : "invariant failed at end of setLogicalId."; 
        }
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
         * A public version of the data structure's internal VertexEntry class.
         * This class is only used for testing.
         */
        public static class VertexEntry extends SortledtonGraph.VertexEntry {
            // Even if Eclipse suggests it: do not add any fields to this class!

            /**
             * Create a VertexEntry with default values.
             */
            public VertexEntry() {
                super();
            }

            /**
             * Create a VertexEntry with the given values.
             * @param logicalId logical ID for the vertex.
             * @param adjacencySet adjacency set value.
             * @param adjacencySetSize size of the adjacency set.
             */
            public VertexEntry(int logicalId, int adjacencySet, int adjacencySetSize) {
                super();
                this.logicalId = logicalId;
                this.adjacencySet = adjacencySet;
                this.adjacencySetSize = adjacencySetSize;
            }

            /**
             * Change the logical ID in the VertexEntry.
             * @param logicalId new logical ID.
             */
            public void setLogicalId(int logicalId) {
                this.logicalId = logicalId;
            }

            /**
             * Change the adjacencySet in the VertexEntry.
             * @param adjacencySet new adjacencySet value.
             */
            public void setAdjacencySet(int adjacencySet) {
                this.adjacencySet = adjacencySet;
            }

            /**
             * Change the adjacencySetSize in the VertexEntry.
             * @param adjacencySetSize new adjacencySetSize value.
             */
            public void setAdjacencySetSize(int adjacencySetSize) {
                this.adjacencySetSize = adjacencySetSize;
            }
        }

        /**
         * Create a debugging instance of the SortledtonGraph with a particular data structure.
         * @param vertexCount the vertex count.
         * @param logicalToPhysical the logicalToPhysical map.
         * @param index the index array.
         * @return a new instance of a SortledtonGraph with the given data structure.
         */
        public static <U> SortledtonGraph<U> newInstance(int vertexCount, Map<Integer, Integer> logicalToPhysical, VertexEntry[] index) {
            SortledtonGraph<U> result = new SortledtonGraph<>();
            result.vertexCount = vertexCount;
            result.logicalToPhysical = new HashMap<>(logicalToPhysical);
            // Clone the index array and assign it to the result.
            result.index = new SortledtonGraph.VertexEntry[index.length];
            for (int i = 0; i < index.length; i++) {
                result.index[i] = index[i];
            }
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
