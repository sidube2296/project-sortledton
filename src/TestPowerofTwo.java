import junit.framework.TestCase;
import edu.uwm.cs351.PowerofTwo;
import java.util.List; //TODO is this needed?

public class TestPowerofTwo extends TestCase {

	private PowerofTwo<String> vector;

	// Setup method to initialize the vector before each test
	protected void setUp() {
		vector = new PowerofTwo<>();
	}

	// Test method for adding a single neighbor
	public void testAddNeighborSingleElement() {
		vector.addNeighbor("A");
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	// Test method for adding multiple neighbors
	public void testAddNeighborMultipleElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		assertEquals(3, vector.size());
		//TODO correct syntax
		//assertTrue(vector.getNeighbors().containsAll(List.of("A", "B", "C")));
	}

	// Test method for resizing
	public void testAddNeighborResizing() {
		for (int i = 0; i < 16; i++) {
			vector.addNeighbor("Element" + i);
		}
		assertEquals(16, vector.size());
		vector.addNeighbor("Extra");
		assertEquals(17, vector.size());
	}

	// Test method for null element
	public void testAddNeighborNullElement() {
		try {
			vector.addNeighbor(null);
			fail("Expected IllegalArgumentException for null element.");
		} catch (IllegalArgumentException e) {
			assertEquals("Element cannot be null", e.getMessage());
		}
	}

	// Test method for removing an element
	public void testRemoveNeighborSingleElement() {
		vector.addNeighbor("A");
		vector.removeNeighbor("A");
		assertEquals(0, vector.size());
		assertFalse(vector.getNeighbors().contains("A"));
	}

	// Test method for removing a nonexistent element
	public void testRemoveNeighborNonexistentElement() {
		vector.addNeighbor("A");
		vector.removeNeighbor("B");
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	// Test method for removing all elements
	public void testRemoveNeighborAllElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.removeNeighbor("A");
		vector.removeNeighbor("B");
		vector.removeNeighbor("C");
		assertEquals(0, vector.size());
	}
}
