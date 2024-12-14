package edu.uwm.cs351;

import java.util.function.Consumer;

/**
 * VertexRecord represents a vertex in the SortledtonGraph, containing its logical ID,
 * adjacency set (neighbors), and the size of the adjacency set.
 *
 * @param <T> The type of the vertex ID.
 */
public class VertexRecord<T extends Comparable<T>> {
    public Neighborhood<T> adjacencySet; // Pointer to the Neighborhood object (PowerofTwo or UnrolledSkipList)
    public int logicalId; // The hash code for the vertex
    public int adjacencySetSize; // Number of neighbors in the adjacency set

    private static Consumer<WellFormedError> reporter = (s) -> System.out.println("Invariant error: " + s.toString());

    private boolean report(WellFormedError error) {
        reporter.accept(error);
        return false;
    }
	
	/**
	 * Holds a set of possible WellFormed error messages.
	 */
	public enum WellFormedError {
		LOGICAL_ID("logicalID cannot be negative"),
		ADJACENCY_SET("the adjacency set must be initialized (non-null)"),
		ADJACENCY_SET_SIZE("the adjacency set's size must be non-negative"),
		ADJACENCY_SET_MISMATCH("adjacencySetSize does not match the actual number of neighbors.");

		private String message; // Message for the enum key

		WellFormedError(String message) { this.message = message; }
		
		@Override // Implementation
		public String toString() { return this.message; }
	}

    /**
     * Checks that the VertexRecord invariant is correctly adhered to.
     * 
     * @return true when in compliance with all listed invariants
     */
    private boolean wellFormed() {
        // 1. logicalID must be greater than or equal to 0
        if (logicalId < 0) return report(WellFormedError.LOGICAL_ID);
        // 2. the adjacencySet must be properly initialized (non-null)
        if (adjacencySet == null) return report(WellFormedError.ADJACENCY_SET);

        // 3. the adjacency set size must not be negative.
        if (adjacencySetSize < 0) return report(WellFormedError.ADJACENCY_SET_SIZE);

        // 4. adjacencySetSize must match the actual number of neighbors
        if (adjacencySetSize != adjacencySet.getNeighbors().size()) return report(WellFormedError.ADJACENCY_SET_MISMATCH);

        return true;
    }

    /**
     * Initializes a new VertexRecord with an empty adjacency set.
     */
    public VertexRecord() {
        adjacencySetSize = 0;
        adjacencySet = new PowerofTwo<T>(); // Default to PowerofTwo for small neighborhoods
        // logicalId should be set separately
        assert wellFormed() : "invariant failed at end of VertexRecord constructor.";
    }

    /**
     * Constructor for VertexRecord with specified logical ID and adjacency set.
     *
     * @param logicalId    The logical ID of the vertex.
     * @param adjacencySet The adjacency set (neighbors) of the vertex.
     */
    public VertexRecord(int logicalId, Neighborhood<T> adjacencySet) {
        this.logicalId = logicalId;
        this.adjacencySet = adjacencySet;
        this.adjacencySetSize = adjacencySet.getNeighbors().size();
        assert wellFormed() : "invariant failed at end of VertexRecord constructor.";
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
     * Sets the logical ID for this vertex.
     *
     * @param logicalId the logical ID to set
     */
    public void setLogicalId(int logicalId) {
        assert wellFormed() : "invariant failed at start of setLogicalId.";
        this.logicalId = logicalId;
        assert wellFormed() : "invariant failed at end of setLogicalId.";
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
		public Consumer<WellFormedError> getReporter() {
			return reporter;
		}
		
		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<WellFormedError> r) {
			reporter = r;
		}
		
		/**
		 * Create a debugging instance of the VertexRecord with the given field values
		 * @param adjacencySet Pointer to the Neighborhood object (PowerofTwo or UnrolledSkipList)
		 * @param logicalId The hash code for the vertex
		 * @param adjacencySetSize Number of neighbors in the adjacency set
		 * @return
		 */
		public <U extends Comparable<U>> VertexRecord<U> newInstance(Neighborhood<U> adjacencySet, int logicalId, int adjacencySetSize) {
			VertexRecord<U> newRecord = new VertexRecord<U>();
			newRecord.adjacencySet = adjacencySet;
			newRecord.logicalId = logicalId;
			newRecord.adjacencySetSize = adjacencySetSize;
			
			return newRecord;
		}
		
		/**
		 * Return whether the debugging instance meets the requirements on the invariant.
		 * 
		 * @param instance instance of VertexRecord to use, must not be null.
		 * @return whether it passes the check.
		 */
		public boolean wellFormed(VertexRecord<?> instance) {
			return instance.wellFormed();
		}
	}
}
