import junit.framework.TestCase;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import edu.uwm.cs351.Neighborhood;
import edu.uwm.cs351.SortledtonGraph;
import edu.uwm.cs351.SortledtonGraph.Spy;
import edu.uwm.cs351.VertexRecord;

public class TestInternals extends TestCase {

    private SortledtonGraph<Integer> graph;

    // Setup method to initialize the graph before each test
    protected void setUp() {
        graph = new SortledtonGraph<>();
    }

    // Test method for creating a SortledtonGraph instance using Spy
    public void testSortledtonGraphInstance() {
        Map<Integer, Integer> logicalToPhysical = new HashMap<>();
    	
    	@SuppressWarnings("unchecked") // We know that it's safe to cast as VertexRecord<Integer>[] here because we're constructing the array directly
        VertexRecord<Integer>[] index = (VertexRecord<Integer>[]) Array.newInstance(VertexRecord.class, 0);
        
        SortledtonGraph<Integer> instance = Spy.newInstance(0, logicalToPhysical, index);
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

    // Test method for inserting multiple vertices
    public void testInsertMultipleVertices() {
        for (int i = 1; i <= 10; i++) {
            graph.insertVertex(i);
            assertTrue(Spy.wellFormed(graph));
        }
    }

    // Test method for inserting and removing vertices
    public void testInsertAndRemoveVertices() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.deleteVertex(1);
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for adding and removing edges
    public void testAddAndRemoveEdges() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertEdge(1, 2);
        graph.deleteEdge(1, 2);
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for verifying wellFormed after deleting all vertices
    public void testDeleteAllVertices() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.deleteVertex(1);
        graph.deleteVertex(2);
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for checking multiple edges from the same vertex
    public void testMultipleEdgesFromSameVertex() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertVertex(3);
        graph.insertEdge(1, 2);
        graph.insertEdge(1, 3);
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for verifying consistency after multiple insertions and deletions
    public void testMultipleInsertionsDeletions() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertEdge(1, 2);
        graph.deleteEdge(1, 2);
        graph.deleteVertex(1);
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for inserting duplicate vertices
    public void testInsertDuplicateVertices() {
        graph.insertVertex(1);
        try {
            graph.insertVertex(1);
            fail("Expected IllegalStateException for duplicate vertex.");
        } catch (IllegalStateException e) {
            // Expected behavior
        }
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for inserting edge with non-existent vertices
    public void testInsertEdgeNonExistentVertices() {
        try {
            graph.insertEdge(1, 2);
            fail("Expected IllegalArgumentException for non-existent vertices.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for deleting non-existent vertex
    public void testDeleteNonExistentVertex() {
        try {
            graph.deleteVertex(1);
            fail("Expected IllegalArgumentException for non-existent vertex.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for deleting edge with non-existent vertices
    public void testDeleteEdgeNonExistentVertices() {
        graph.insertVertex(1);
        try {
            graph.deleteEdge(1, 2);
            fail("Expected IllegalArgumentException for non-existent destination vertex.");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for checking vertex count consistency
    public void testVertexCountConsistency() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        assertEquals(2, graph.getVertexCount());
        graph.deleteVertex(1);
        assertEquals(1, graph.getVertexCount());
        assertTrue(Spy.wellFormed(graph));
    }

    // Test method for verifying wellFormed after clearing the graph
    public void testClearGraph() {
        graph.insertVertex(1);
        graph.insertVertex(2);
        graph.insertEdge(1, 2);
        graph.deleteVertex(1);
        graph.deleteVertex(2);
        assertTrue(Spy.wellFormed(graph));
    }
    
}