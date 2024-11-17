import junit.framework.TestCase;

import java.util.HashMap;

import edu.uwm.cs351.SortledtonGraph;
import edu.uwm.cs351.SortledtonGraph.Spy;
import edu.uwm.cs351.SortledtonGraph.Spy.VertexEntry;


public class TestInternals extends TestCase {

	private SortledtonGraph<Integer> graph;

	// Setup method to initialize the graph before each test
	protected void setUp() {
		graph = new SortledtonGraph<>();
	}


	// Test method for creating a SortledtonGraph instance using Spy
		public void testSortledtonGraphInstance() {
			SortledtonGraph<Integer> instance = Spy.newInstance(0, new HashMap<Integer, Integer>(), new VertexEntry[0]);
			boolean isWellFormed = Spy.wellFormed(instance);
			assertTrue(isWellFormed);
		}

	// Test method for verifying wellFormed() invariants
		public void testGraphInvariant() {
			assertTrue(Spy.wellFormed(graph));
			graph.insertVertex(1);
			assertTrue(Spy.wellFormed(graph));
			graph.insertEdge(1, 2);
			assertTrue(Spy.wellFormed(graph));
		}


}