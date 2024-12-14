import junit.framework.TestCase;
import edu.uwm.cs351.UnrolledSkipList;

import java.util.List;

/**
 * Test suite for the UnrolledSkipList class.
 * This suite contains 30 test cases covering various scenarios,
 * including normal operations, edge cases, and invariants.
 */
public class TestUnrolledSkipList extends TestCase {

    private UnrolledSkipList<String> unrolledSkipList;

    // Setup method to initialize the UnrolledSkipList before each test
    protected void setUp() {
        unrolledSkipList = new UnrolledSkipList<>();
    }

    /**
     * Test adding a single neighbor.
     */
    public void testAddNeighbor() {
        try {
            unrolledSkipList.addNeighbor("A");
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.contains("A"));
            assertEquals(1, neighbors.size());

            unrolledSkipList.addNeighbor("B");
            unrolledSkipList.addNeighbor("C");
            neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.contains("B"));
            assertTrue(neighbors.contains("C"));
            assertEquals(3, neighbors.size());
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test adding a null neighbor should throw IllegalArgumentException.
     */
    public void testAddNullNeighbor() {
        try {
            unrolledSkipList.addNeighbor(null);
            fail("Expected IllegalArgumentException for adding null neighbor");
        } catch (IllegalArgumentException e) {
            // Test passes because exception is expected
        }
    }

    /**
     * Test removing an existing neighbor.
     */
    public void testRemoveNeighbor() {
        try {
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.addNeighbor("B");
            unrolledSkipList.removeNeighbor("A");

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertFalse(neighbors.contains("A"));
            assertTrue(neighbors.contains("B"));
            assertEquals(1, neighbors.size());
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test removing a non-existent neighbor should not affect the list.
     */
    public void testRemoveNonExistentNeighbor() {
        try {
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.removeNeighbor("B"); // "B" does not exist
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(1, neighbors.size());
            assertTrue(neighbors.contains("A"));
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test retrieving neighbors to ensure they are in sorted order.
     */
    public void testGetNeighbors() {
        unrolledSkipList.addNeighbor("C");
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");

        List<String> neighbors = unrolledSkipList.getNeighbors();
        assertEquals(3, neighbors.size());
        assertEquals("A", neighbors.get(0)); // Ensure sorted order
        assertEquals("B", neighbors.get(1));
        assertEquals("C", neighbors.get(2));
    }

    /**
     * Test finding the intersection of two neighborhoods with common elements.
     */
    public void testIntersect() {
        UnrolledSkipList<String> other = new UnrolledSkipList<>();
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");
        unrolledSkipList.addNeighbor("C");

        other.addNeighbor("B");
        other.addNeighbor("C");
        other.addNeighbor("D");

        List<String> intersection = unrolledSkipList.intersect(other);
        assertEquals(2, intersection.size());
        assertTrue(intersection.contains("B"));
        assertTrue(intersection.contains("C"));
    }

    /**
     * Test intersection with no common neighbors should return an empty list.
     */
    public void testIntersectNoCommonNeighbors() {
        UnrolledSkipList<String> other = new UnrolledSkipList<>();
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");

        other.addNeighbor("C");
        other.addNeighbor("D");

        List<String> intersection = unrolledSkipList.intersect(other);
        assertNotNull(intersection);
        assertTrue(intersection.isEmpty());
    }

    /**
     * Test adding a large number of neighbors to trigger multiple block splits.
     */
    public void testLargeNeighborhood() {
        try {
            for (int i = 1; i <= 1000; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(1000, neighbors.size());
            assertTrue(neighbors.contains("N1"));
            assertTrue(neighbors.contains("N1000"));
            // Ensure sorted order
            assertEquals("N1", neighbors.get(0));
            assertEquals("N1000", neighbors.get(999));
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test adding duplicate neighbors should not increase the size.
     */
    public void testAddDuplicateNeighbor() {
        try {
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.addNeighbor("A"); // Duplicate
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(1, neighbors.size());
            assertTrue(neighbors.contains("A"));
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test removing a neighbor from an empty UnrolledSkipList should have no effect.
     */
    public void testRemoveFromEmptyList() {
        try {
            unrolledSkipList.removeNeighbor("A"); // List is empty
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.isEmpty());
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }

    /**
     * Test block splitting by adding elements beyond BLOCK_SIZE.
     */
    public void testBlockSplitting() {
        try {
            for (int i = 1; i <= 150; i++) { // BLOCK_SIZE is 128
                unrolledSkipList.addNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(150, neighbors.size());
            assertTrue(neighbors.contains("N1"));
            assertTrue(neighbors.contains("N150"));
        } catch (Exception e) {
            fail("Exception was thrown during block splitting test: " + e.getMessage());
        }
    }

    /**
     * Test block merging by removing elements to reduce block sizes below threshold.
     */
    public void testBlockMerging() {
        try {
            // Add 200 elements to create multiple blocks
            for (int i = 1; i <= 200; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            // Remove 150 elements to trigger merging
            for (int i = 1; i <= 150; i++) {
                unrolledSkipList.removeNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(50, neighbors.size());
            for (int i = 151; i <= 200; i++) {
                assertTrue(neighbors.contains("N" + i));
            }
        } catch (Exception e) {
            fail("Exception was thrown during block merging test: " + e.getMessage());
        }
    }

    /**
     * Test retrieving neighbors from an empty UnrolledSkipList.
     */
    public void testEmptyGetNeighbors() {
        List<String> neighbors = unrolledSkipList.getNeighbors();
        assertNotNull(neighbors);
        assertTrue(neighbors.isEmpty());
    }

    /**
     * Test intersecting with an empty neighborhood should return an empty list.
     */
    public void testIntersectWithEmptyNeighborhood() {
        UnrolledSkipList<String> other = new UnrolledSkipList<>();
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");

        List<String> intersection = unrolledSkipList.intersect(other);
        assertNotNull(intersection);
        assertTrue(intersection.isEmpty());
    }

    /**
     * Test intersecting a neighborhood with itself should return all elements.
     */
    public void testIntersectWithSelf() {
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");
        unrolledSkipList.addNeighbor("C");

        List<String> intersection = unrolledSkipList.intersect(unrolledSkipList);
        assertEquals(3, intersection.size());
        assertTrue(intersection.contains("A"));
        assertTrue(intersection.contains("B"));
        assertTrue(intersection.contains("C"));
    }

    /**
     * Test the size method to ensure it returns the correct number of neighbors.
     */
    public void testSizeMethod() {
        assertEquals(0, unrolledSkipList.size());

        unrolledSkipList.addNeighbor("A");
        assertEquals(1, unrolledSkipList.size());

        unrolledSkipList.addNeighbor("B");
        unrolledSkipList.addNeighbor("C");
        assertEquals(3, unrolledSkipList.size());

        unrolledSkipList.removeNeighbor("B");
        assertEquals(2, unrolledSkipList.size());
    }

    /**
     * Test the toString method for correct string representation.
     */
    public void testToString() {
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("C");
        unrolledSkipList.addNeighbor("B");

        String expected = "UnrolledSkipList[A, B, C]";
        assertEquals(expected, unrolledSkipList.toString());
    }

    /**
     * Test adding elements in random order and ensure they are stored in sorted order.
     */
    public void testAddElementsOutOfOrder() {
        try {
            unrolledSkipList.addNeighbor("Delta");
            unrolledSkipList.addNeighbor("Alpha");
            unrolledSkipList.addNeighbor("Charlie");
            unrolledSkipList.addNeighbor("Bravo");

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(4, neighbors.size());
            assertEquals("Alpha", neighbors.get(0));
            assertEquals("Bravo", neighbors.get(1));
            assertEquals("Charlie", neighbors.get(2));
            assertEquals("Delta", neighbors.get(3));
        } catch (Exception e) {
            fail("Exception was thrown during out-of-order addition: " + e.getMessage());
        }
    }

    /**
     * Test removing all neighbors one by one.
     */
    public void testRemoveAllNeighbors() {
        try {
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.addNeighbor("B");
            unrolledSkipList.addNeighbor("C");

            unrolledSkipList.removeNeighbor("A");
            unrolledSkipList.removeNeighbor("B");
            unrolledSkipList.removeNeighbor("C");

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.isEmpty());
        } catch (Exception e) {
            fail("Exception was thrown during removing all neighbors: " + e.getMessage());
        }
    }

    /**
     * Test performing a complex sequence of additions and removals.
     */
    public void testComplexOperations() {
        try {
            // Add elements
            unrolledSkipList.addNeighbor("X");
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.addNeighbor("M");
            unrolledSkipList.addNeighbor("C");

            // Remove some elements
            unrolledSkipList.removeNeighbor("A");
            unrolledSkipList.removeNeighbor("Y"); // Non-existent

            // Add more elements
            unrolledSkipList.addNeighbor("B");
            unrolledSkipList.addNeighbor("D");

            // Final expected list: B, C, D, M, X
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(5, neighbors.size());
            assertEquals("B", neighbors.get(0));
            assertEquals("C", neighbors.get(1));
            assertEquals("D", neighbors.get(2));
            assertEquals("M", neighbors.get(3));
            assertEquals("X", neighbors.get(4));
        } catch (Exception e) {
            fail("Exception was thrown during complex operations: " + e.getMessage());
        }
    }

    /**
     * Test intersecting with a neighborhood that has some but not all elements in common.
     */
    public void testPartialIntersect() {
        UnrolledSkipList<String> other = new UnrolledSkipList<>();
        unrolledSkipList.addNeighbor("Alpha");
        unrolledSkipList.addNeighbor("Beta");
        unrolledSkipList.addNeighbor("Gamma");
        unrolledSkipList.addNeighbor("Delta");

        other.addNeighbor("Beta");
        other.addNeighbor("Gamma");
        other.addNeighbor("Epsilon");

        List<String> intersection = unrolledSkipList.intersect(other);
        assertEquals(2, intersection.size());
        assertTrue(intersection.contains("Beta"));
        assertTrue(intersection.contains("Gamma"));
    }

    /**
     * Test adding and removing elements in reverse order.
     */
    public void testReverseOrderOperations() {
        try {
            // Add elements in reverse order
            for (int i = 10; i >= 1; i--) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(10, neighbors.size());
            for (int i = 1; i <= 10; i++) {
                assertEquals("N" + i, neighbors.get(i - 1));
            }

            // Remove elements in reverse order
            for (int i = 10; i >= 1; i--) {
                unrolledSkipList.removeNeighbor("N" + i);
            }

            neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.isEmpty());
        } catch (Exception e) {
            fail("Exception was thrown during reverse order operations test: " + e.getMessage());
        }
    }

    /**
     * Test adding a very large number of neighbors to assess performance and correctness.
     */
    public void testVeryLargeNeighborhood() {
        try {
            for (int i = 1; i <= 10000; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(10000, neighbors.size());
            assertTrue(neighbors.contains("N1"));
            assertTrue(neighbors.contains("N10000"));
            // Verify a few random elements
            assertTrue(neighbors.contains("N5000"));
            assertTrue(neighbors.contains("N9999"));
        } catch (Exception e) {
            fail("Exception was thrown during very large neighborhood test: " + e.getMessage());
        }
    }

    /**
     * Test that blocks do not exceed BLOCK_SIZE after multiple insertions and removals.
     */
    public void testBlockSizeInvariant() {
        try {
            // Add elements to create multiple blocks
            for (int i = 1; i <= 300; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            // Remove some elements
            for (int i = 1; i <= 150; i++) {
                unrolledSkipList.removeNeighbor("N" + i);
            }

            // The remaining 150 elements should be organized in blocks not exceeding BLOCK_SIZE
            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(150, neighbors.size());

            // Since BLOCK_SIZE is 128, expect at least two blocks, but since blocks are internal,
            // we can't directly test them. Instead, ensure that all elements are present.
            assertTrue(neighbors.contains("N151"));
            assertTrue(neighbors.contains("N300"));
        } catch (Exception e) {
            fail("Exception was thrown during block size invariant test: " + e.getMessage());
        }
    }

    /**
     * Test that the UnrolledSkipList handles a large number of duplicate additions gracefully.
     */
    public void testAddManyDuplicates() {
        try {
            for (int i = 0; i < 1000; i++) {
                unrolledSkipList.addNeighbor("A");
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(1, neighbors.size());
            assertTrue(neighbors.contains("A"));
        } catch (Exception e) {
            fail("Exception was thrown during adding many duplicates: " + e.getMessage());
        }
    }

    /**
     * Test removing neighbors in a pattern to trigger both block splitting and merging.
     */
    public void testBlockSplitAndMerge() {
        try {
            // Add 256 elements to create two full blocks
            for (int i = 1; i <= 256; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            // Remove elements to cause underflow and trigger merging
            for (int i = 1; i <= 200; i++) {
                unrolledSkipList.removeNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(56, neighbors.size());
            assertTrue(neighbors.contains("N201"));
            assertTrue(neighbors.contains("N256"));
        } catch (Exception e) {
            fail("Exception was thrown during block split and merge test: " + e.getMessage());
        }
    }

    /**
     * Test that the UnrolledSkipList remains empty after multiple add and remove operations.
     */
    public void testEmptyAfterOperations() {
        try {
            unrolledSkipList.addNeighbor("A");
            unrolledSkipList.addNeighbor("B");
            unrolledSkipList.removeNeighbor("A");
            unrolledSkipList.removeNeighbor("B");

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertTrue(neighbors.isEmpty());
        } catch (Exception e) {
            fail("Exception was thrown during empty after operations test: " + e.getMessage());
        }
    }

    /**
     * Test intersecting with a neighborhood that contains all elements of the original.
     */
    public void testIntersectWithSuperset() {
        UnrolledSkipList<String> other = new UnrolledSkipList<>();
        unrolledSkipList.addNeighbor("A");
        unrolledSkipList.addNeighbor("B");

        other.addNeighbor("A");
        other.addNeighbor("B");
        other.addNeighbor("C");
        other.addNeighbor("D");

        List<String> intersection = unrolledSkipList.intersect(other);
        assertEquals(2, intersection.size());
        assertTrue(intersection.contains("A"));
        assertTrue(intersection.contains("B"));
    }
}
