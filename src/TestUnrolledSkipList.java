import junit.framework.TestCase;
import edu.uwm.cs351.UnrolledSkipList;

import java.util.List;

public class TestUnrolledSkipList extends TestCase {

    private UnrolledSkipList<String> unrolledSkipList;

    // Setup method to initialize the UnrolledSkipList before each test
    protected void setUp() {
        unrolledSkipList = new UnrolledSkipList<>();
    }

    // Test method for adding a neighbor
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

    // Test method for adding a null neighbor
    public void testAddNullNeighbor() {
        try {
            unrolledSkipList.addNeighbor(null);
            fail("Expected IllegalArgumentException for adding null neighbor");
        } catch (IllegalArgumentException e) {
            // Test passes because exception is expected
        }
    }

    // Test method for removing a neighbor
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

    // Test method for removing a non-existent neighbor
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

    // Test method for retrieving neighbors
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

    // Test method for finding the intersection of two neighborhoods
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

    // Test method for intersection with no common neighbors
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

    // Test method for a large number of neighbors
    public void testLargeNeighborhood() {
        try {
            for (int i = 1; i <= 1000; i++) {
                unrolledSkipList.addNeighbor("N" + i);
            }

            List<String> neighbors = unrolledSkipList.getNeighbors();
            assertEquals(1000, neighbors.size());
            assertTrue(neighbors.contains("N1"));
            assertTrue(neighbors.contains("N1000"));
        } catch (Exception e) {
            fail("Exception was thrown: " + e.getMessage());
        }
    }
}
