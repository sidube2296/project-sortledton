import junit.framework.TestCase;
import edu.uwm.cs351.SortledtonGraph;


public class TestSortledtonGraph extends TestCase {

	private SortledtonGraph<Integer> graph;

	// Setup method to initialize the graph before each test
	protected void setUp() {
		graph = new SortledtonGraph<>();
	}


	// Test method for inserting an edge between two vertices
	public void testInsertEdge() {
		try {
			graph.insertEdge(1, 2);
			assertTrue(graph.hasVertex(1));
			assertTrue(graph.hasVertex(2));
		} catch (Exception e) {
			fail("Exception was thrown: " + e.getMessage());
		}
	}

	// Test method for deleting an edge between two vertices
	public void testDeleteEdge() {
		graph.insertEdge(1, 2);
		try {
			graph.deleteEdge(1, 2);
			// Verify that edge does not exist anymore
			assertFalse(graph.findEdge(1, 2));
		} catch (Exception e) {
			fail("Exception was thrown: " + e.getMessage());
		}
	}

	// Test method for finding an edge
	public void testFindEdge() {
		graph.insertEdge(1, 2);
		assertTrue(graph.findEdge(1, 2));
		assertFalse(graph.findEdge(2, 3));
	}

	// Test method for inserting a vertex
	public void testInsertVertex() {
		try {
			graph.insertVertex(3);
			assertTrue(graph.hasVertex(3));
		} catch (Exception e) {
			fail("Exception was thrown: " + e.getMessage());
		}
	}

	// Test method for deleting a vertex
	public void testDeleteVertex() {
		graph.insertVertex(3);
		graph.deleteVertex(3);
		assertFalse(graph.hasVertex(3));
	}

}