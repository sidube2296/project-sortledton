import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

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
	
	public void testNullArguments() {
		try {
			graph.insertVertex(null);
			fail("Expected IllegalArgumentException for null vertex insertion.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for insertVertex(null): " + e);
		}

		try {
			graph.insertEdge(null, 2);
			fail("Expected IllegalArgumentException for null srcId in insertEdge.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for insertEdge(null,2): " + e);
		}

		try {
			graph.findEdge(null, 2);
			fail("Expected IllegalArgumentException for null srcId in findEdge.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for findEdge(null,2): " + e);
		}

		try {
			graph.getNeighbors(null);
			fail("Expected IllegalArgumentException for null argument in getNeighbors.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for getNeighbors(null): " + e);
		}

		try {
			graph.deleteEdge(null, 2);
			fail("Expected IllegalArgumentException for null srcId in deleteEdge.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for deleteEdge(null,2): " + e);
		}

		try {
			graph.deleteVertex(null);
			fail("Expected IllegalArgumentException for deleteVertex(null).");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception for deleteVertex(null): " + e);
		}
	}

	public void testGetNeighbors() {
		try {
			graph.insertEdge(1, 2);
			graph.insertEdge(1, 3);

			List<Integer> neighbors1 = graph.getNeighbors(1);
			assertEquals("Vertex 1 should have 2 neighbors", 2, neighbors1.size());
			assertTrue(neighbors1.contains(2));
			assertTrue(neighbors1.contains(3));

			List<Integer> neighbors2 = graph.getNeighbors(2);
			assertEquals("Vertex 2 should have 1 neighbor (1)", 1, neighbors2.size());
			assertTrue(neighbors2.contains(1));
		} catch (Exception e) {
			fail("Exception was thrown in testGetNeighbors: " + e);
		}
	}

	public void testIntersectNeighbors() {
		try {
			graph.insertEdge(1, 2);
			graph.insertEdge(2, 3);
			graph.insertEdge(1, 3);
			graph.insertEdge(3, 4);

			List<Integer> intersection12 = graph.intersectNeighbors(1, 2);
			assertEquals("Vertices 1,2 should share neighbor 3", 1, intersection12.size());
			assertTrue(intersection12.contains(3));

			List<Integer> intersection23 = graph.intersectNeighbors(2, 3);
			assertEquals("Vertices 2,3 share neighbor 1", 1, intersection23.size());
			assertTrue(intersection23.contains(1));

			List<Integer> intersection14 = graph.intersectNeighbors(1, 4);
			assertEquals("Vertices 1,4 share neighbor 3", 1, intersection14.size());
			assertTrue(intersection14.contains(3));

			List<Integer> intersection34 = graph.intersectNeighbors(3, 4);
			assertTrue("Vertices 3,4 share no other common neighbor (besides each other)", intersection34.isEmpty());
		} catch (Exception e) {
			fail("Exception was thrown in testIntersectNeighbors: " + e);
		}
	}

	public void testScanNeighbors() {
		try {
			graph.insertEdge(1, 2);
			graph.insertEdge(1, 3);

			final ArrayList<Integer> visited = new ArrayList<>();
			graph.scanNeighbors(1, n -> visited.add(n));

			assertEquals("Vertex 1 should have neighbors [2,3]", 2, visited.size());
			assertTrue(visited.contains(2));
			assertTrue(visited.contains(3));
		} catch (Exception e) {
			fail("Exception was thrown in testScanNeighbors: " + e);
		}
	}

	public void testSelfEdge() {
		try {
			graph.insertEdge(5, 5);
			assertTrue("Vertex 5 should be auto-inserted", graph.hasVertex(5));
			assertTrue("A self-edge means findEdge(5,5) is true", graph.findEdge(5,5));

			List<Integer> neighbors5 = graph.getNeighbors(5);
			assertEquals("Self-edge should appear once in neighbor list", 1, neighbors5.size());
			assertTrue(neighbors5.contains(5));
		} catch (Exception e) {
			fail("Exception was thrown in testSelfEdge: " + e);
		}
	}

	public void testRepeatedEdgeInsertion() {
		try {
			graph.insertEdge(1, 2);
			graph.insertEdge(1, 2);
			graph.insertEdge(1, 2);

			assertTrue("Edge (1,2) should exist", graph.findEdge(1,2));
			List<Integer> neighbors1 = graph.getNeighbors(1);
			assertEquals("1 should have exactly 1 neighbor (2)", 1, neighbors1.size());
			assertTrue(neighbors1.contains(2));

			List<Integer> neighbors2 = graph.getNeighbors(2);
			assertEquals("2 should have exactly 1 neighbor (1)", 1, neighbors2.size());
			assertTrue(neighbors2.contains(1));
		} catch (Exception e) {
			fail("Exception was thrown in testRepeatedEdgeInsertion: " + e);
		}
	}

    public void testInsertVertexTwice() {
        try {
            graph.insertVertex(10);
            graph.insertVertex(10);
            fail("Expected IllegalStateException for inserting vertex 10 twice.");
        } catch (IllegalStateException e) {
        } catch (Exception e) {
            fail("Wrong exception for inserting vertex 10 twice: " + e);
        }
    }

    public void testDeleteNonExistentVertex() {
        try {
            graph.deleteVertex(999);
            fail("Expected IllegalArgumentException for deleting non-existent vertex 999.");
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail("Wrong exception for deleting non-existent vertex: " + e);
        }
    }

    public void testMultipleEdges() {
        try {
            graph.insertEdge(1, 2);
            graph.insertEdge(2, 3);
            graph.insertEdge(1, 4);
            graph.insertEdge(4, 2);

            List<Integer> neighbors1 = graph.getNeighbors(1);
            assertEquals("Vertex 1 should have 2 neighbors", 2, neighbors1.size());
            assertTrue(neighbors1.contains(2));
            assertTrue(neighbors1.contains(4));

            List<Integer> neighbors2 = graph.getNeighbors(2);
            assertEquals("Vertex 2 should have 3 neighbors", 3, neighbors2.size());
            assertTrue(neighbors2.contains(1));
            assertTrue(neighbors2.contains(3));
            assertTrue(neighbors2.contains(4));

            graph.deleteEdge(4, 2);
            neighbors2 = graph.getNeighbors(2);
            assertEquals("Vertex 2 should have 2 neighbors after deleting (4,2)", 2, neighbors2.size());
            assertFalse(neighbors2.contains(4));
            assertTrue(neighbors2.contains(1));
            assertTrue(neighbors2.contains(3));

        } catch (Exception e) {
            fail("Exception in testMultipleEdges: " + e);
        }
    }

    public void testDeleteEdgeNonExistent() {
        try {
            graph.deleteEdge(10, 99);
            fail("Expected IllegalArgumentException for deleting edge (10,99) with non-existent vertices.");
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail("Wrong exception for deleteEdge(10,99): " + e);
        }
    }

    public void testMassInsertDelete() {
        try {
            for (int i = 1; i <= 5; i++) {
                for (int j = i+1; j <= 5; j++) {
                    graph.insertEdge(i, j);
                }
            }
            for (int i = 1; i <= 5; i++) {
                for (int j = i+1; j <= 5; j++) {
                    assertTrue("Edge (" + i + "," + j + ") should exist.", graph.findEdge(i, j));
                }
            }

            graph.deleteEdge(1, 2);
            graph.deleteEdge(4, 5);

            assertFalse("Edge (1,2) should no longer exist.", graph.findEdge(1, 2));
            assertFalse("Edge (4,5) should no longer exist.", graph.findEdge(4, 5));
            assertTrue("Edge (1,3) still exists", graph.findEdge(1, 3));

        } catch (Exception e) {
            fail("Exception was thrown in testMassInsertDelete: " + e);
        }
    }
    
    public void testReInsertAfterDeleteVertex() {
		try {
			graph.insertVertex(10);
			graph.deleteVertex(10);
			assertFalse("Vertex 10 should be gone", graph.hasVertex(10));

			graph.insertVertex(10);
			assertTrue("Vertex 10 should be inserted again", graph.hasVertex(10));
		} catch (Exception e) {
			fail("Exception in testReInsertAfterDeleteVertex: " + e);
		}
	}

	public void testDeleteEdgeTwice() {
		graph.insertEdge(1, 2);
		try {
			graph.deleteEdge(1, 2);
			assertFalse("Edge (1,2) should not exist now", graph.findEdge(1, 2));
			graph.deleteEdge(1, 2); 
			assertFalse("Edge (1,2) should still not exist", graph.findEdge(1, 2));
		} catch (Exception e) {
			fail("Exception in testDeleteEdgeTwice: " + e);
		}
	}

	public void testLargeVertexInsert() {
		int N = 200;
		try {
			for (int i = 0; i < N; i++) {
				graph.insertVertex(i);
			}
			assertEquals("All inserted vertices should exist", N, graph.getVertexCount());
			for (int i = 0; i < N; i++) {
				assertTrue("Vertex " + i + " should exist", graph.hasVertex(i));
			}
		} catch (Exception e) {
			fail("Exception in testLargeVertexInsert: " + e);
		}
	}

	public void testEdgeOnDeletedVertices() {
		try {
			graph.insertVertex(1);
			graph.insertVertex(2);
			graph.deleteVertex(1);
			graph.deleteVertex(2);
			assertFalse(graph.hasVertex(1));
			assertFalse(graph.hasVertex(2));

			graph.insertEdge(1, 2);
			assertTrue(graph.hasVertex(1));
			assertTrue(graph.hasVertex(2));
			assertTrue("Edge (1,2) should now exist", graph.findEdge(1, 2));
		} catch (Exception e) {
			fail("Exception in testEdgeOnDeletedVertices: " + e);
		}
	}

	public void testNeighborsAfterDeleteVertex() {
		try {
			graph.insertEdge(10, 20);
			graph.insertEdge(10, 30);
			graph.deleteVertex(10);
			assertFalse("Vertex 10 should be deleted", graph.hasVertex(10));

			List<Integer> neighbors20 = graph.getNeighbors(20);
			assertFalse("Vertex 20 neighbors should not include 10", neighbors20.contains(10));

			List<Integer> neighbors30 = graph.getNeighbors(30);
			assertFalse("Vertex 30 neighbors should not include 10", neighbors30.contains(10));
		} catch (Exception e) {
			fail("Exception in testNeighborsAfterDeleteVertex: " + e);
		}
	}

	public void testScanNeighborsAfterEdgeDeletion() {
		graph.insertEdge(1, 2);
		graph.insertEdge(1, 3);

		graph.deleteEdge(1, 2);
		final ArrayList<Integer> visited = new ArrayList<>();
		try {
			graph.scanNeighbors(1, visited::add);
			assertEquals("After deleting (1,2), vertex 1 should have only neighbor 3", 1, visited.size());
			assertTrue(visited.contains(3));
		} catch (Exception e) {
			fail("Exception in testScanNeighborsAfterEdgeDeletion: " + e);
		}
	}

	public void testIntersectNeighborsNonExistentVertices() {
		try {
			graph.intersectNeighbors(1, 2);
			fail("Expected IllegalArgumentException for non-existent vertices in intersectNeighbors.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception: " + e);
		}

		graph.insertVertex(1);
		try {
			graph.intersectNeighbors(1, 2);
			fail("Expected IllegalArgumentException when second vertex doesn't exist.");
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			fail("Wrong exception: " + e);
		}
	}

	public void testInsertEdgeMultipleDestinations() {
		try {
			graph.insertEdge(5, 6);
			graph.insertEdge(5, 7);
			graph.insertEdge(5, 8);

			List<Integer> neighborsOf5 = graph.getNeighbors(5);
			assertEquals("Vertex 5 should have 3 neighbors now", 3, neighborsOf5.size());
			assertTrue(neighborsOf5.contains(6));
			assertTrue(neighborsOf5.contains(7));
			assertTrue(neighborsOf5.contains(8));
		} catch (Exception e) {
			fail("Exception in testInsertEdgeMultipleDestinations: " + e);
		}
	}

	public void testDeleteAllVertices() {
		try {
			graph.insertEdge(1, 2);
			graph.insertEdge(3, 4);
			graph.insertEdge(2, 3);

			graph.deleteVertex(1);
			graph.deleteVertex(2);
			graph.deleteVertex(3);
			graph.deleteVertex(4);

			assertEquals("All vertices should be deleted now", 0, graph.getVertexCount());
			assertFalse(graph.hasVertex(1));
			assertFalse(graph.hasVertex(2));
			assertFalse(graph.hasVertex(3));
			assertFalse(graph.hasVertex(4));
		} catch (Exception e) {
			fail("Exception in testDeleteAllVertices: " + e);
		}
	}

	public void testPhysicalAndLogicalIdMappings() {
		try {
			graph.insertVertex(100);
			graph.insertVertex(200);
			graph.insertVertex(300);

			Integer p100 = graph.physicalId(100);
			Integer p200 = graph.physicalId(200);
			Integer p300 = graph.physicalId(300);
			assertNotNull("physicalId(100) should not be null", p100);
			assertNotNull("physicalId(200) should not be null", p200);
			assertNotNull("physicalId(300) should not be null", p300);

			assertEquals("logicalId(p100) should return 100", (Integer)100, graph.logicalId(p100));
			assertEquals("logicalId(p200) should return 200", (Integer)200, graph.logicalId(p200));
			assertEquals("logicalId(p300) should return 300", (Integer)300, graph.logicalId(p300));
		} catch (Exception e) {
			fail("Exception in testPhysicalAndLogicalIdMappings: " + e);
		}
	}
}