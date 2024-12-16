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
	
	//================== Tests for conversion between neighborhood types =========================
	
	/**
	 * Test which starts with a PowerofTwo (1 edge) and grows to 129, which should cause the 
	 * conversion to UnrolledSkipList
	 */
	public void testConvertToUnrolledSkipList() {
	    graph.insertVertex(1);

	    for (int i = 2; i <= 130; i++) {
	        graph.insertEdge(1, i);
	    }

	    List<Integer> neighbors = graph.getNeighbors(1);
	    assertEquals("Vertex 1 should have 129 neighbors", 129, neighbors.size());

	    // a couple of checks to ensure no data lost
	    assertTrue("Vertex 1 should still contain neighbor 2", neighbors.contains(2));
	    assertTrue("Vertex 1 should still contain neighbor 130", neighbors.contains(130));
	}

	/**
	 * The inverse of the preceding test. Starts with 130 edges to make it an UnrolledSkipList.
	 * Then, remove edges to below BLOCK_SIZE to conver back to PowerofTwo
	 */
	public void testConvertBackToPowerOfTwo() {
	    graph.insertVertex(1);
	    for (int i = 2; i <= 130; i++) {
	        graph.insertEdge(1, i);
	    }

	    for (int i = 130; i > 2; i--) {
	        graph.deleteEdge(1, i);
	    }

	    List<Integer> neighbors = graph.getNeighbors(1);
	    assertEquals("After removing edges, vertex 1 should have only 1 neighbor", 1, neighbors.size());
	    assertEquals("The only neighbor should be vertex 2", (Integer)2, neighbors.get(0));
	}
		
	//================== Tests for empty graph and intersection of degree 0 vertices =============
	
	/**
	 * start with an empty graph and check calling various methods on it to check the associated
	 * exceptions are thrown.
	 */
	public void testEmptyGraphOperations() {
	    assertEquals("Vertex count should be 0 in an empty graph", 0, graph.getVertexCount());
	    assertFalse("Empty graph should not have vertex 1", graph.hasVertex(1));
	    
	    try {
	        graph.getNeighbors(1);
	        fail("Expected IllegalArgumentException for getNeighbors() on non-existent vertex in empty graph.");
	    } catch (IllegalArgumentException e) {
	        // expected
	    }

	    assertFalse("findEdge() should return false on empty graph", graph.findEdge(1, 2));
	    
	    try {
	        graph.intersectNeighbors(1, 2);
	        fail("Expected IllegalArgumentException for intersectNeighbors() on empty graph.");
	    } catch (IllegalArgumentException e) {
	        // expected
	    }

	    try {
	        graph.scanNeighbors(1, n -> fail("Should not visit any neighbor on empty graph"));
	        fail("Expected IllegalArgumentException for scanNeighbors() on non-existent vertex in empty graph.");
	    } catch (IllegalArgumentException e) {
	        // expected
	    }
	}
	
	/**
	 * Intersection test (including various other methods) on a graph with 2 vertices but no edges.
	 */
	public void testNoNeighborsVertices() {
	    graph.insertVertex(1);
	    graph.insertVertex(2);

	    List<Integer> neighbors1 = graph.getNeighbors(1);
	    assertTrue("Vertex 1 should have no neighbors", neighbors1.isEmpty());

	    List<Integer> neighbors2 = graph.getNeighbors(2);
	    assertTrue("Vertex 2 should have no neighbors", neighbors2.isEmpty());

	    List<Integer> intersection = graph.intersectNeighbors(1, 2);
	    assertTrue("Intersection should be empty when both vertices have no neighbors", intersection.isEmpty());
	    
	    final ArrayList<Integer> visited = new ArrayList<>();
	    graph.scanNeighbors(1, visited::add);
	    assertTrue("No neighbors should be visited for vertex 1", visited.isEmpty());
	}
	
	/**
	 * Intersection test for 2 vertices with edges but nothing in common
	 */
	public void testNoCommonNeighbors() {
	    // Set up a scenario where 2 vertices have neighbors, but no overlap.
	    // Vertex 1 connects to {2,3}, Vertex 4 connects to {5,6}, no common neighbors.
	    graph.insertEdge(1, 2);
	    graph.insertEdge(1, 3);
	    graph.insertEdge(4, 5);
	    graph.insertEdge(4, 6);

	    List<Integer> neighbors1 = graph.getNeighbors(1);
	    assertEquals("Vertex 1 should have 2 neighbors", 2, neighbors1.size());
	    assertTrue(neighbors1.contains(2) && neighbors1.contains(3));

	    List<Integer> neighbors4 = graph.getNeighbors(4);
	    assertEquals("Vertex 4 should have 2 neighbors", 2, neighbors4.size());
	    assertTrue(neighbors4.contains(5) && neighbors4.contains(6));

	    List<Integer> intersection = graph.intersectNeighbors(1, 4);
	    assertTrue("No common neighbors between vertex 1 and 4", intersection.isEmpty());
	}
	
	//================== Tests for additional edge cases and unusual states ======================
	
	/**
	 * test removal of a self-loop (and edge between a vertex and itself)
	 * Note: self-loops are allowed in our data structure
	 */
	public void testSelfLoopRemoval() {
	    graph.insertEdge(5, 5);
	    assertTrue("Self-loop (5,5) should exist", graph.findEdge(5,5));

	    graph.deleteEdge(5,5);
	    assertFalse("Self-loop (5,5) should be removed", graph.findEdge(5,5));
	}
	
	/**
	 * Test insertion of duplicate edges. They should not be replicated, as we do not allow repeats
	 * of an edge in our data structure.
	 */
	public void testDuplicateEdgeInsertion() {
	    graph.insertEdge(1, 2);
	    graph.insertEdge(1, 2);
	    graph.insertEdge(1, 2);

	    List<Integer> neighbors1 = graph.getNeighbors(1);
	    assertEquals("Vertex 1 should have exactly one neighbor (2) even after duplicates", 
	                 1, neighbors1.size());
	    assertTrue("That neighbor should be vertex 2", neighbors1.contains(2));
	}

	/**
	 * Test several unusual vertex IDs: vertex and edge insertion
	 */
	public void testNonSequentialVertexInsertion() {
	    graph.insertVertex(-10);
	    graph.insertVertex(999999999);
	    graph.insertVertex(42);

	    assertTrue("Vertex -10 should exist", graph.hasVertex(-10));
	    assertTrue("Vertex 999999999 should exist", graph.hasVertex(999999999));
	    assertTrue("Vertex 42 should exist", graph.hasVertex(42));

	    graph.insertEdge(-10, 999999999);
	    assertTrue("Edge (-10, 999999999) should exist", graph.findEdge(-10, 999999999));
	}

	/**
	 * Unusual sequence: Insert a vertex, delete it, reinsert it, and add an edge
	 */
	public void testEdgesAfterDeleteAndReinsert() {
	    // Insert and delete a vertex
	    graph.insertVertex(10);
	    graph.deleteVertex(10);
	    assertFalse("Vertex 10 should be deleted", graph.hasVertex(10));

	    // Re-insert vertex 10, and add an edge 
	    graph.insertVertex(10);
	    graph.insertVertex(20);
	    graph.insertEdge(10, 20);
	    assertTrue("Edge (10,20) should exist after reinserting vertex 10", 
	               graph.findEdge(10, 20));

	    // Check neighbors
	    List<Integer> neighbors10 = graph.getNeighbors(10);
	    assertEquals("After re-insertion, vertex 10 should have 1 neighbor", 1, neighbors10.size());
	    assertTrue(neighbors10.contains(20));
	}
	
	/**
	 * Test deleting and edge and then try deleting it again
	 */
	public void testDeletingNonExistentEdge() {
	    graph.insertVertex(1);
	    graph.insertVertex(2);
	    graph.insertEdge(1, 2);
	    
	    graph.deleteEdge(1, 2);
	    assertFalse("Edge (1,2) should be deleted", graph.findEdge(1, 2));

	    // Try deleting the same edge again - should fail
	    try {
	        graph.deleteEdge(1, 2);
	        fail("Expected IllegalArgumentException for deleting non-existent edge (1,2) second time.");
	    } catch (IllegalArgumentException e) {
	        // expected
	    }
	}
	
	/**
	 * Try a sequence of edge insertions, scan, delete edge, and re-scan
	 */
	public void testMultipleScansAndActions() {
	    // Insert edges so vertex 1 connects to multiple neighbors
	    graph.insertEdge(1, 2);
	    graph.insertEdge(1, 3);
	    graph.insertEdge(1, 4);

	    final ArrayList<Integer> visited = new ArrayList<>();
	    graph.scanNeighbors(1, visited::add);
	    assertEquals("Vertex 1 should have three neighbors", 3, visited.size());
	    assertTrue(visited.contains(2) && visited.contains(3) && visited.contains(4));

	    // Scan again, but remove an edge between scans
	    visited.clear();
	    graph.deleteEdge(1, 3);
	    graph.scanNeighbors(1, visited::add);
	    assertEquals("After deleting (1,3), vertex 1 should have two neighbors", 2, visited.size());
	    assertTrue(visited.contains(2) && visited.contains(4));
	    assertFalse(visited.contains(3));
	}
	
	//================== Tests for intersectNeighbors ============================================
	
	/**
	 * Self intersection of a vertex
	 */
	public void testIntersectNeighborsSelf() {
	    graph.insertEdge(1, 2);
	    graph.insertEdge(1, 3);
	    graph.insertEdge(1, 4);

	    List<Integer> intersection = graph.intersectNeighbors(1, 1);
	    List<Integer> neighbors = graph.getNeighbors(1);
	    assertEquals("Intersection of a vertex with itself should be all its neighbors", 
	                 neighbors.size(), intersection.size());
	    assertTrue("Intersection should contain all neighbors", intersection.containsAll(neighbors));
	}

	/**
	 * Test a typical partial overlap of neighbors between 2 vertices
	 */
	public void testIntersectNeighborsPartialOverlap() {
	    for (int i = 2; i <= 6; i++) {
	        graph.insertEdge(1, i);
	    }
	    graph.insertEdge(10, 4);
	    graph.insertEdge(10, 5);
	    graph.insertEdge(10, 7);
	    graph.insertEdge(10, 8);

	    List<Integer> intersection = graph.intersectNeighbors(1, 10);
	    assertEquals("Vertices 1 and 10 should share exactly two neighbors", 2, intersection.size());
	    assertTrue(intersection.contains(4));
	    assertTrue(intersection.contains(5));
	}

	/**
	 * Test disjointed neighborhoods
	 */
	public void testIntersectNeighborsNoOverlap() {
	    graph.insertEdge(2, 20);
	    graph.insertEdge(2, 21);
	    graph.insertEdge(2, 22);

	    graph.insertEdge(3, 30);
	    graph.insertEdge(3, 31);
	    graph.insertEdge(3, 32);

	    List<Integer> intersection = graph.intersectNeighbors(2, 3);
	    assertTrue("No common neighbors between vertices 2 and 3", intersection.isEmpty());
	}

	/**
	 * Test with 1 empty neighborhood
	 */
	public void testIntersectNeighborsOneEmpty() {
	    graph.insertEdge(4, 5);
	    graph.insertEdge(4, 6);
	    graph.insertVertex(7); // no edges

	    List<Integer> intersection = graph.intersectNeighbors(4, 7);
	    assertTrue("No intersection when one vertex has no neighbors", intersection.isEmpty());
	}

	/**
	 * test an intersection between vertices that do not exist in the graph - should throw an IAE
	 */
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

	/**
	 * test intersections of neighborhoods with significantly different degrees
	 */
	public void testIntersectNeighborsVariousSizes() {
	    int A = 1;
	    int B = 2;

	    // Vertex A connects to {3,4,...,20}
	    for (int i = 3; i <= 20; i++) {
	        graph.insertEdge(A, i);
	    }

	    // Vertex B connects to {10,15,100}
	    graph.insertEdge(B, 10);
	    graph.insertEdge(B, 15);
	    graph.insertEdge(B, 100);

	    // The intersection should be {10,15}
	    List<Integer> intersection = graph.intersectNeighbors(A, B);
	    assertEquals("There should be 2 common neighbors", 2, intersection.size());
	    assertTrue(intersection.contains(10));
	    assertTrue(intersection.contains(15));
	}
}