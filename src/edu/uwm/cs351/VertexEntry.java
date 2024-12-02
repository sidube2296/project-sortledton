package edu.uwm.cs351;

import java.util.function.Consumer;

/**
 * An entry in the vertex index that stores information about a vertex.
 */
public class VertexEntry<T extends Comparable<T>> {
	// See figure 6 of the paper: and individual element of the Adjacency Index
	public Neighborhood<T> adjacencySet; // pointer to the Neighborhood object (PowerofTwo or UnrolledSkipList)
	public int logicalId; // the hash code for the vertex
	public int adjacencySetSize; // Number of neighbors in the adjacency set

	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: " + s);

	private boolean report(String error) {
		reporter.accept(error);
		return false;
	}

	/**
	 * Checks that the VertexEntry invariant is correctly adhered to.
	 * 
	 * @return true when in compliance with all listed invariants
	 */
	private boolean wellFormed() {
		// 1. logicalID must be greater than or equal to 0

		if (logicalId < 0)
			return report("logicalID cannot be negative.");
		// 2. the adjacencySet must be properly initialized (non null)
		if (adjacencySet == null)
			return report("the adjacency set must be initialized (non-null)");

		// 3. the adjacency set size must not be negative.
		if (adjacencySetSize < 0)
			return report("the adjacency set's size must be non-negative");

		return true;
	}

	/**
	 * Initializes a new VertexEntry, with adjacency set size set to zero. The
	 * adjacency set represents the number of neighbors connected to this vertex.
	 */
	public VertexEntry() {
		adjacencySetSize = 0;
		adjacencySet = new PowerofTwo<T>(); // Default to PowerofTwo for small neighborhoods
		assert wellFormed() : "invariant failed at end of VertexEntry constructor.";
	}

	/**
	 * Constructor for VertexEntry with specified values for the fields
	 */
	public VertexEntry(int logicalId, Neighborhood<T> adjacencySet) {
		this.logicalId = logicalId;
		this.adjacencySet = adjacencySet;
		this.adjacencySetSize = adjacencySet.getNeighbors().size();
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

	/*
	 * public static class Spy { /** Return the sink for invariant error messages.
	 * 
	 * @return current reporter.
	 * 
	 * public Consumer<String> getReporter() { return reporter; }
	 * 
	 *//**
		 * Change the sink for invariant error messages.
		 * 
		 * @param r where to send invariant error messages.
		 */
	/*
	 * public void setReporter(Consumer<String> r) { reporter = r; }
	 * 
	 *//**
		 * Create a VertexEntry with the given values.
		 * 
		 * @param logicalId        logical ID for the vertex.
		 * @param adjacencySet     adjacency set value.
		 * @param adjacencySetSize size of the adjacency set.
		 */
	/*
	 * public VertexEntry newInstance(int logicalId, Neighborhood<T> adjacencySet,
	 * int adjacencySetSize) { super(); this.logicalId = logicalId;
	 * this.adjacencySet = adjacencySet; this.adjacencySetSize = adjacencySetSize; }
	 * 
	 * 
	 *//**
		 * Change the logical ID in the VertexEntry.
		 * 
		 * @param logicalId new logical ID.
		 */
	/*
	 * public void setLogicalId(int logicalId) { this.logicalId = logicalId; }
	 * 
	 *//**
		 * Change the adjacencySet in the VertexEntry.
		 * 
		 * @param adjacencySet new adjacencySet value.
		 */
	/*
	 * public void setAdjacencySet(int adjacencySet) { //TODO fix this compile error
	 * //this.adjacencySet = adjacencySet; }
	 * 
	 *//**
		 * Change the adjacencySetSize in the VertexEntry.
		 * 
		 * @param adjacencySetSize new adjacencySetSize value.
		 *//*
			 * public void setAdjacencySetSize(int adjacencySetSize) { this.adjacencySetSize
			 * = adjacencySetSize; } }
			 */
}
