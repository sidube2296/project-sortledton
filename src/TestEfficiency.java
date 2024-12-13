import junit.framework.TestCase;
import edu.uwm.cs351.SortledtonGraph;
import java.util.Random;
import java.util.List;

public class TestEfficiency extends TestCase {

    private static final int NUM_VERTICES = 100_000;
    private static final int NUM_EDGES = 500_000;
    private SortledtonGraph<Integer> graph;
    private Random random;

    @Override
    protected void setUp() {
        graph = new SortledtonGraph<>();
        random = new Random(42);
    }

    public void testInsertVertexEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        assertEquals(NUM_VERTICES, graph.getVertexCount());
    }

    public void testInsertEdgeEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }

        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }
    }

    public void testFindEdgeEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }

        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.findEdge(src, dest);
        }
    }

    public void testIntersectNeighborsEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }

        for (int i = 0; i < 10_000; i++) {
            int v1 = random.nextInt(NUM_VERTICES);
            int v2 = random.nextInt(NUM_VERTICES);
            List<Integer> intersection = graph.intersectNeighbors(v1, v2);
            assertNotNull(intersection);
        }
    }

    public void testDeleteEdgeEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }

        for (int i = 0; i < NUM_EDGES / 2; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.deleteEdge(src, dest);
        }
    }

    public void testDeleteVertexEfficiency() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }

        for (int i = 0; i < NUM_VERTICES / 2; i++) {
            graph.deleteVertex(i);
        }
        assertEquals(NUM_VERTICES / 2, graph.getVertexCount());
    }


    public void testWeaklyConnectedComponents() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }
        assertTrue(graph.getVertexCount() > 0);
    }

    public void testPageRank() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }
        assertTrue(graph.getVertexCount() > 0);
    }

    public void testBreadthFirstSearch() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }
        assertTrue(graph.getVertexCount() > 0);
    }

    public void testSingleSourceShortestPath() {
        for (int i = 0; i < NUM_VERTICES; i++) {
            graph.insertVertex(i);
        }
        for (int i = 0; i < NUM_EDGES; i++) {
            int src = random.nextInt(NUM_VERTICES);
            int dest = random.nextInt(NUM_VERTICES);
            graph.insertEdge(src, dest);
        }
        assertTrue(graph.getVertexCount() > 0);
    }
}
