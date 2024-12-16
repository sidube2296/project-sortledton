import junit.framework.TestCase;
import edu.uwm.cs351.SortledtonGraph;

public class TestCapacityGrowth extends TestCase {
	/**
	 * The authors proposed a quite large initial capacity (131_072) due to the size of graphs
	 * Sortledton is intended for. This method rises beyond that level to ensure that
	 * ensureCapacity functions as intended. 
	 * 
	 * This test has been broken out due to the extensive time required to complete it.
	 * !!! THIS IS A LONG ONE !!!
	 */
	public void testEnsureCapacityGrowth() {
		SortledtonGraph<Integer> graph = new SortledtonGraph<>();
		
		int originalSize = graph.getVertexCount();
	    int triggerCount = 140000;

	        for (int i = 1; i <= triggerCount; i++) {
	            graph.insertVertex(i);
	            System.out.println(i);	//Make sure we're still inserting vertices to the index as expected
	        }

	        assertEquals("All inserted vertices should be counted", originalSize + triggerCount, graph.getVertexCount());

	        assertTrue("Vertex 100000 should exist", graph.hasVertex(100000));
	        assertTrue("Vertex 200000 should exist", graph.hasVertex(130000));
	}
}
