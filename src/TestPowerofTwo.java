import junit.framework.TestCase;
import edu.uwm.cs351.PowerofTwo;
import java.util.List;
import java.lang.reflect.Field;


/**
 * TestPowerofTwo is a JUnit 3-style test class that thoroughly tests 
 * the PowerofTwo implementation of the Neighborhood interface.
 * 
 * We have expanded the number of test cases from the original 16 to 40 
 * to cover a wide range of scenarios, including edge cases, large inputs, 
 * random orders, and various intersection checks.
 */
public class TestPowerofTwo extends TestCase {

	private PowerofTwo<String> vector;
	private Field neighborsField;

	/**
	 * Setup method to initialize the vector before each test.
	 */
	protected void setUp() throws Exception {
        super.setUp();
        vector = new PowerofTwo<>();
        // Access the private 'neighbors' field using reflection
        neighborsField = PowerofTwo.class.getDeclaredField("neighbors");
        neighborsField.setAccessible(true);
    }


	/**
	 * Test adding a single neighbor and verifying that 
	 * the size and content are as expected.
	 */
	public void testAddNeighborSingleElement() {
		vector.addNeighbor("A");
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	/**
	 * Test adding multiple neighbors and ensuring the size is correct.
	 */
	public void testAddNeighborMultipleElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		assertEquals(3, vector.size());
	}

	/**
	 * Test adding neighbors to observe if the resizing logic (if any) works.
	 * In this case, we just add more than a small number to ensure no errors occur.
	 */
	public void testAddNeighborResizing() {
		for (int i = 0; i < 16; i++) {
			vector.addNeighbor("Element" + i);
		}
		assertEquals(16, vector.size());
		vector.addNeighbor("Extra");
		assertEquals(17, vector.size());
	}

	/**
	 * Test that adding a null neighbor throws the expected exception.
	 */
	public void testAddNeighborNullElement() {
		try {
			vector.addNeighbor(null);
			fail("Expected IllegalArgumentException for null element.");
		} catch (IllegalArgumentException e) {
			assertEquals("Element cannot be null", e.getMessage());
		}
	}

	/**
	 * Test removing a single element that exists.
	 */
	public void testRemoveNeighborSingleElement() {
		vector.addNeighbor("A");
		vector.removeNeighbor("A");
		assertEquals(0, vector.size());
		assertFalse(vector.getNeighbors().contains("A"));
	}

	/**
	 * Test removing a nonexistent element and ensuring no changes are made.
	 */
	public void testRemoveNeighborNonexistentElement() {
		vector.addNeighbor("A");
		vector.removeNeighbor("B");
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	/**
	 * Test removing all elements that were previously added.
	 */
	public void testRemoveNeighborAllElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.removeNeighbor("A");
		vector.removeNeighbor("B");
		vector.removeNeighbor("C");
		assertEquals(0, vector.size());
	}

	/**
	 * Test that adding a duplicate element does not increase the size or create duplicates.
	 */
	public void testAddNeighborDuplicateElement() {
		vector.addNeighbor("A");
		vector.addNeighbor("A");
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	/**
	 * Test removing an element from an empty structure, should have no effect.
	 */
	public void testRemoveNeighborFromEmpty() {
		vector.removeNeighbor("A");
		assertEquals(0, vector.size());
	}

	/**
	 * Test intersecting two empty neighborhoods, should return empty list.
	 */
	public void testIntersectEmptyNeighborhoods() {
		PowerofTwo<String> otherVector = new PowerofTwo<>();
		List<String> intersection = vector.intersect(otherVector);
		assertTrue(intersection.isEmpty());
	}

	/**
	 * Test intersection where there are no common elements.
	 */
	public void testIntersectNoCommonElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		PowerofTwo<String> otherVector = new PowerofTwo<>();
		otherVector.addNeighbor("C");
		otherVector.addNeighbor("D");
		List<String> intersection = vector.intersect(otherVector);
		assertTrue(intersection.isEmpty());
	}

	/**
	 * Test intersection where some elements are common.
	 */
	public void testIntersectSomeCommonElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		PowerofTwo<String> otherVector = new PowerofTwo<>();
		otherVector.addNeighbor("B");
		otherVector.addNeighbor("C");
		otherVector.addNeighbor("D");
		List<String> intersection = vector.intersect(otherVector);
		assertEquals(2, intersection.size());
		assertTrue(intersection.contains("B"));
		assertTrue(intersection.contains("C"));
	}

	/**
	 * Test intersection where all elements are common.
	 */
	public void testIntersectAllCommonElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		PowerofTwo<String> otherVector = new PowerofTwo<>();
		otherVector.addNeighbor("A");
		otherVector.addNeighbor("B");
		otherVector.addNeighbor("C");
		List<String> intersection = vector.intersect(otherVector);
		assertEquals(3, intersection.size());
	}

	/**
	 * Test removing a neighbor and ensuring the order of the remaining neighbors is still sorted.
	 */
	public void testRemoveNeighborSortedOrder() {
		vector.addNeighbor("C");
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.removeNeighbor("B");
		assertEquals(2, vector.size());
		assertFalse(vector.getNeighbors().contains("B"));
		// Order should still be A, C
		List<String> neighbors = vector.getNeighbors();
		assertEquals("A", neighbors.get(0));
		assertEquals("C", neighbors.get(1));
	}

	/**
	 * Test adding and removing repeatedly in various orders.
	 */
	public void testAddAndRemoveRepeatedly() {
		vector.addNeighbor("A");
		vector.removeNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.removeNeighbor("B");
		vector.addNeighbor("D");
		assertEquals(2, vector.size());
		assertTrue(vector.getNeighbors().contains("C"));
		assertTrue(vector.getNeighbors().contains("D"));
	}

	/**
	 * Test intersection with partially overlapping ranges of elements.
	 */
	public void testIntersectPartiallyOverlappingRanges() {
		for (int i = 0; i < 10; i++) {
			vector.addNeighbor("Element" + i);
		}
		PowerofTwo<String> otherVector = new PowerofTwo<>();
		for (int i = 5; i < 15; i++) {
			otherVector.addNeighbor("Element" + i);
		}
		List<String> intersection = vector.intersect(otherVector);
		assertEquals(5, intersection.size());
		for (int i = 5; i < 10; i++) {
			assertTrue(intersection.contains("Element" + i));
		}
	}

	// ---------------- NEW TESTS START HERE (17 to 40) ----------------

	/**
	 * Test that the neighbors are always kept sorted after multiple insertions.
	 */
	public void testGetNeighborsIsSorted() {
		vector.addNeighbor("Z");
		vector.addNeighbor("M");
		vector.addNeighbor("A");
		List<String> sorted = vector.getNeighbors();
		assertEquals("A", sorted.get(0));
		assertEquals("M", sorted.get(1));
		assertEquals("Z", sorted.get(2));
	}

	/**
	 * Test adding a large number of elements (e.g., 1000) 
	 * and ensure they are all present and sorted.
	 */
	public void testAddLargeNumberOfElements() {
		for (int i = 999; i >= 0; i--) {
			vector.addNeighbor("E" + i);
		}
		assertEquals(1000, vector.size());
		// Check first and last to ensure sorting
		List<String> n = vector.getNeighbors();
		assertEquals("E0", n.get(0));
		assertEquals("E999", n.get(n.size()-1));
	}

	/**
	 * Test adding elements in strictly descending order 
	 * and verify they end up sorted ascending.
	 */
	public void testAddInDescendingOrder() {
		vector.addNeighbor("D");
		vector.addNeighbor("C");
		vector.addNeighbor("B");
		vector.addNeighbor("A");
		List<String> sorted = vector.getNeighbors();
		assertEquals("A", sorted.get(0));
		assertEquals("B", sorted.get(1));
		assertEquals("C", sorted.get(2));
		assertEquals("D", sorted.get(3));
	}

	/**
	 * Test removing the first (smallest) element from a multi-element vector.
	 */
	public void testRemoveFirstElement() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.removeNeighbor("A");
		assertFalse(vector.getNeighbors().contains("A"));
		assertEquals(2, vector.size());
	}

	/**
	 * Test removing the last (largest) element from a multi-element vector.
	 */
	public void testRemoveLastElement() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.removeNeighbor("C");
		assertFalse(vector.getNeighbors().contains("C"));
		assertEquals(2, vector.size());
	}

	/**
	 * Test removing an element from the middle of a multi-element set.
	 */
	public void testRemoveMiddleElement() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		vector.addNeighbor("D");
		vector.removeNeighbor("C");
		assertFalse(vector.getNeighbors().contains("C"));
		assertEquals(3, vector.size());
		List<String> n = vector.getNeighbors();
		assertEquals("A", n.get(0));
		assertEquals("B", n.get(1));
		assertEquals("D", n.get(2));
	}

	/**
	 * Test intersecting the vector with itself should return the same set.
	 */
	public void testIntersectSelf() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		List<String> intersection = vector.intersect(vector);
		assertEquals(3, intersection.size());
		assertTrue(intersection.contains("A"));
		assertTrue(intersection.contains("B"));
		assertTrue(intersection.contains("C"));
	}

	/**
	 * Test intersect where one vector is a subset of the other.
	 */
	public void testIntersectWithSubset() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		PowerofTwo<String> subset = new PowerofTwo<>();
		subset.addNeighbor("B");
		List<String> intersection = vector.intersect(subset);
		assertEquals(1, intersection.size());
		assertTrue(intersection.contains("B"));
	}

	/**
	 * Test intersect where one vector is a superset of the other.
	 */
	public void testIntersectWithSuperset() {
		vector.addNeighbor("B");
		PowerofTwo<String> superset = new PowerofTwo<>();
		superset.addNeighbor("A");
		superset.addNeighbor("B");
		superset.addNeighbor("C");
		List<String> intersection = vector.intersect(superset);
		assertEquals(1, intersection.size());
		assertTrue(intersection.contains("B"));
	}

	/**
	 * Test intersection when the other neighborhood might have duplicates added repeatedly 
	 * (though our structure ignores duplicates, let's ensure intersection has no duplicates).
	 */
	public void testIntersectWithDuplicateElements() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		PowerofTwo<String> other = new PowerofTwo<>();
		other.addNeighbor("B");
		other.addNeighbor("B");
		other.addNeighbor("B");
		List<String> intersection = vector.intersect(other);
		assertEquals(1, intersection.size());
		assertTrue(intersection.contains("B"));
	}

	/**
	 * Test repeatedly attempting to add null elements 
	 * (always should throw IllegalArgumentException).
	 */
	public void testAddNullRepeatedAttempts() {
		for (int i = 0; i < 5; i++) {
			try {
				vector.addNeighbor(null);
				fail("Should always fail for null");
			} catch (IllegalArgumentException e) {
				// expected
			}
		}
		assertEquals(0, vector.size());
	}

	/**
	 * Test attempting to remove a null element. This will attempt binarySearch 
	 * with null and likely cause a NullPointerException.
	 * We expect a NullPointerException since compareTo will be called on null.
	 */
	public void testRemoveNull() {
		vector.addNeighbor("A");
		try {
			vector.removeNeighbor(null);
			fail("Expected NullPointerException when removing null.");
		} catch (NullPointerException e) {
			// expected
		}
		assertEquals(1, vector.size());
		assertTrue(vector.getNeighbors().contains("A"));
	}

	/**
	 * Test intersecting an empty neighborhood with a non-empty one.
	 * Result should be empty.
	 */
	public void testIntersectWithEmptyAndNonEmpty() {
		vector.addNeighbor("X");
		PowerofTwo<String> empty = new PowerofTwo<>();
		List<String> intersection = vector.intersect(empty);
		assertTrue(intersection.isEmpty());
	}

	/**
	 * Test that getNeighbors returns a copy. Modifying the returned list should 
	 * not affect the internal state of vector.
	 */
	public void testGetNeighborsReturnsCopy() {
		vector.addNeighbor("A");
		List<String> copy = vector.getNeighbors();
		copy.add("B");
		// The original should not have changed.
		assertEquals(1, vector.size());
		assertFalse(vector.getNeighbors().contains("B"));
	}

	/**
	 * Test large intersection operations with many elements.
	 */
	public void testLargeIntersect() {
		for (int i = 0; i < 50; i++) {
			vector.addNeighbor("N" + i);
		}
		PowerofTwo<String> other = new PowerofTwo<>();
		for (int i = 25; i < 75; i++) {
			other.addNeighbor("N" + i);
		}
		List<String> intersection = vector.intersect(other);
		assertEquals(25, intersection.size());
		for (int i = 25; i < 50; i++) {
			assertTrue(intersection.contains("N" + i));
		}
	}

	/**
	 * Test inserting elements in a random order, 
	 * ensuring the final structure is sorted.
	 */
	public void testRandomOrderInsertion() {
		String[] elems = {"M", "Z", "A", "R", "K"};
		for (String s : elems) {
			vector.addNeighbor(s);
		}
		List<String> sorted = vector.getNeighbors();
		assertEquals("A", sorted.get(0));
		assertEquals("K", sorted.get(1));
		assertEquals("M", sorted.get(2));
		assertEquals("R", sorted.get(3));
		assertEquals("Z", sorted.get(4));
	}

	/**
	 * Test removing all elements in random order and ensuring correctness.
	 */
	public void testRemoveAllInRandomOrder() {
		String[] elems = {"X", "Y", "Z", "A", "C"};
		for (String s : elems) {
			vector.addNeighbor(s);
		}
		vector.removeNeighbor("Z");
		vector.removeNeighbor("A");
		vector.removeNeighbor("Y");
		vector.removeNeighbor("X");
		vector.removeNeighbor("C");
		assertEquals(0, vector.size());
	}

	/**
	 * Test adding multiple duplicates and then removing them.
	 * Even if duplicates are ignored at addition, ensure that after removal 
	 * the element is truly gone.
	 */
	public void testDuplicatesRemoval() {
		vector.addNeighbor("A");
		vector.addNeighbor("A");
		vector.addNeighbor("A");
		assertEquals(1, vector.size());
		vector.removeNeighbor("A");
		assertEquals(0, vector.size());
	}

	/**
	 * Test adding strings of different lengths and ensure sorting by lexicographical order.
	 */
	public void testCaseWithStringsOfDifferentLengths() {
	    vector.addNeighbor("Apple");
	    vector.addNeighbor("App");
	    vector.addNeighbor("Banana");
	    vector.addNeighbor("Apex");
	    List<String> sorted = vector.getNeighbors();
	    assertEquals("Apex", sorted.get(0));
	    assertEquals("App", sorted.get(1));
	    assertEquals("Apple", sorted.get(2));
	    assertEquals("Banana", sorted.get(3));
	}

	/**
	 * Test adding a set of already sorted elements and confirm no issues arise.
	 */
	public void testAddNeighborAlreadySortedInput() {
		vector.addNeighbor("A");
		vector.addNeighbor("B");
		vector.addNeighbor("C");
		List<String> n = vector.getNeighbors();
		assertEquals("A", n.get(0));
		assertEquals("B", n.get(1));
		assertEquals("C", n.get(2));
	}

	/**
	 * Test adding an element that should go between existing neighbors, 
	 * ensuring insertion at the correct position.
	 */
	public void testAddElementBetweenExisting() {
		vector.addNeighbor("A");
		vector.addNeighbor("C");
		vector.addNeighbor("B"); // should insert between A and C
		List<String> n = vector.getNeighbors();
		assertEquals("A", n.get(0));
		assertEquals("B", n.get(1));
		assertEquals("C", n.get(2));
	}

	/**
	 * Test removing elements that never existed in a large set. 
	 * Ensures that no side effects occur.
	 */
	public void testRemoveNonExistentEdges() {
		for (int i = 0; i < 20; i++) {
			vector.addNeighbor("X" + i);
		}
		vector.removeNeighbor("X-1");
		vector.removeNeighbor("X20");
		assertEquals(20, vector.size());
	}

	/**
	 * Test intersecting with many common elements.
	 * Both sets have large overlapping ranges.
	 */
	public void testIntersectWithManyCommonElements() {
		for (int i = 0; i < 30; i++) {
			vector.addNeighbor("Y" + i);
		}
		PowerofTwo<String> other = new PowerofTwo<>();
		for (int i = 10; i < 40; i++) {
			other.addNeighbor("Y" + i);
		}
		List<String> intersection = vector.intersect(other);
		assertEquals(20, intersection.size()); // from Y10 to Y29
	}

	/**
	 * Test performing multiple intersections in a row 
	 * and ensure no side-effects on the original sets.
	 */
	public void testMultipleIntersectionsInARow() {
		for (int i = 0; i < 5; i++) {
			vector.addNeighbor("Z" + i);
		}
		PowerofTwo<String> other = new PowerofTwo<>();
		for (int i = 2; i < 7; i++) {
			other.addNeighbor("Z" + i);
		}
		List<String> firstIntersection = vector.intersect(other);
		assertEquals(3, firstIntersection.size()); // Z2, Z3, Z4
		List<String> secondIntersection = vector.intersect(other);
		assertEquals(3, secondIntersection.size()); // no side effects
	}
	
	/**
	 * Test adding neighbors containing Unicode characters (e.g., accented letters).
	 * Ensures that the lexicographical ordering works correctly with extended characters.
	 */
	public void testAddNeighborWithUnicodeCharacters() {
	    vector.addNeighbor("Zèbra");  // Unicode e with grave
	    vector.addNeighbor("Zebra");
	    vector.addNeighbor("ZébRa");  // Capital R, mixed case and accent
	    List<String> sorted = vector.getNeighbors();
	    // Compare their lex order: Typically "Zebra" < "Zèbra" < "ZébRa" based on Unicode order
	    // However, actual ordering depends on character ordering.
	    // Let's just verify that they are sorted consistently:
	    assertTrue(sorted.size() == 3);
	    for (int i = 0; i < sorted.size() - 1; i++) {
	        // This will confirm sorted[i] <= sorted[i+1] lex order
	        assertTrue(sorted.get(i).compareTo(sorted.get(i+1)) <= 0);
	    }
	}

	/**
	 * Test removing elements at the boundaries of a large set.
	 * Adds a range of elements, removes the first and the last repeatedly, ensuring stability.
	 */
	public void testRemoveAtBoundaries() {
	    for (int i = 0; i < 10; i++) {
	        vector.addNeighbor("X" + i);
	    }
	    // Remove first and last elements
	    vector.removeNeighbor("X0");
	    vector.removeNeighbor("X9");
	    assertEquals(8, vector.size());
	    assertFalse(vector.getNeighbors().contains("X0"));
	    assertFalse(vector.getNeighbors().contains("X9"));
	}

	/**
	 * Test intersecting the vector with itself after removing some elements.
	 * Ensures the intersection remains consistent and equals the vector's current state.
	 */
	public void testIntersectionWithItselfAfterRemovals() {
	    vector.addNeighbor("A");
	    vector.addNeighbor("B");
	    vector.addNeighbor("C");
	    vector.removeNeighbor("B");
	    List<String> intersection = vector.intersect(vector); 
	    // Should now just be "A" and "C"
	    assertEquals(2, intersection.size());
	    assertTrue(intersection.contains("A"));
	    assertTrue(intersection.contains("C"));
	    assertFalse(intersection.contains("B"));
	}

	/**
	 * Test intersecting an empty neighborhood with a one-element neighborhood.
	 * Expected intersection: empty, since one side is empty.
	 */
	public void testEmptyIntersectWithOneElement() {
	    PowerofTwo<String> other = new PowerofTwo<>();
	    other.addNeighbor("Single");
	    List<String> intersection = vector.intersect(other);
	    assertTrue(intersection.isEmpty());
	}

	/**
	 * Test intersection when one vector has multiple elements,
	 * and the other has a single element that matches exactly one in the first vector.
	 * Ensures correct single-element intersection.
	 */
	public void testNonEmptyIntersectWithSingleMatchingElement() {
	    vector.addNeighbor("A");
	    vector.addNeighbor("B");
	    vector.addNeighbor("C");
	    PowerofTwo<String> other = new PowerofTwo<>();
	    other.addNeighbor("B");
	    List<String> intersection = vector.intersect(other);
	    assertEquals(1, intersection.size());
	    assertTrue(intersection.contains("B"));
	}

	/**
	 * Test adding an element multiple times to ensure duplicates are not added.
	 * Already tested similar cases, but now we attempt many times repeatedly.
	 */
	public void testAddAlreadyPresentElementMultipleTimes() {
	    vector.addNeighbor("Repeat");
	    vector.addNeighbor("Repeat");
	    vector.addNeighbor("Repeat");
	    assertEquals(1, vector.size());
	    assertTrue(vector.getNeighbors().contains("Repeat"));
	}

	/**
	 * Test removing the same element twice. The second removal should do nothing.
	 * Ensures no exceptions or issues arise on redundant removals.
	 */
	public void testAddElementThenRemoveItTwice() {
	    vector.addNeighbor("ToRemove");
	    vector.removeNeighbor("ToRemove");
	    vector.removeNeighbor("ToRemove"); // Attempt again
	    assertEquals(0, vector.size());
	}

	/**
	 * Test adding both uppercase and lowercase strings to check their lexicographical order.
	 * By default, uppercase letters come before lowercase in ASCII/Unicode, 
	 * so "Apple" < "apple" should hold true.
	 */
	public void testCaseSensitiveOrdering() {
	    vector.addNeighbor("apple");
	    vector.addNeighbor("Apple");
	    vector.addNeighbor("applE");
	    List<String> sorted = vector.getNeighbors();
	    // Check known ordering: "Apple" < "applE" < "apple"
	    // 'A' (65) < 'a' (97), and 'E' < 'e'
	    assertEquals("Apple", sorted.get(0));
	    assertEquals("applE", sorted.get(1));
	    assertEquals("apple", sorted.get(2));
	}

	/**
	 * Test removing all elements in reverse order of addition to confirm stability.
	 * Add several elements, then remove them starting from the last added to the first.
	 */
	public void testRemoveElementsInReverseOrderOfAddition() {
	    vector.addNeighbor("First");
	    vector.addNeighbor("Second");
	    vector.addNeighbor("Third");
	    vector.removeNeighbor("Third");
	    vector.removeNeighbor("Second");
	    vector.removeNeighbor("First");
	    assertEquals(0, vector.size());
	}

	/**
	 * Test a large intersection scenario where both sides have large sets but no common elements.
	 * Ensures intersection is empty and operates efficiently.
	 */
	public void testLargeIntersectWithNoCommonElements() {
	    for (int i = 0; i < 50; i++) {
	        vector.addNeighbor("A" + i); // A0, A1, ..., A49
	    }
	    PowerofTwo<String> other = new PowerofTwo<>();
	    for (int i = 50; i < 100; i++) {
	        other.addNeighbor("A" + i); // A50, A51, ..., A99
	    }
	    List<String> intersection = vector.intersect(other);
	    assertTrue(intersection.isEmpty());
	}
	
	
	/**
	 * These tests attempt to break the invariants maintained by wellformed()
	 * by using reflection to modify the private internal state of PowerofTwo.
	 * 
	 * If wellformed() is implemented correctly, it should detect these problems
	 * when a public method is subsequently called. As a result, these tests will
	 * throw AssertionError if run with assertions enabled.
	 * 
	 * IMPORTANT: These tests are for demonstration only. Normally, you should
	 * not rely on reflection to break encapsulation. This is just to confirm that
	 * wellformed() is indeed working.
	 */
	
	/**
     * INVARIANT CHECK TEST #1:
     * Try inserting a null element into the neighbors list directly.
     * Next time we call a method that triggers wellformed(), 
     * it should fail with an AssertionError.
     */
	public void testInvariantNullElement() throws Exception {
	    @SuppressWarnings("unchecked")
	    List<String> neighbors = (List<String>)neighborsField.get(vector);

	    // Add multiple elements so that the list is big
	    // and our search won't need to touch the end.
	    // We choose letters so '0' is lexicographically less.
	    for (char c = 'A'; c <= 'K'; c++) {
	        vector.addNeighbor(String.valueOf(c));
	    }
	    // neighbors is now ["A", "B", "C", ..., "K"]

	    // Insert a null element illegally at the end of the list
	    neighbors.add(null);

	    try {
	        // Now try to add "0" (zero).
	        // Since "0" < "A", binarySearch will look at the front portion of the array,
	        // never needing to examine the null at the end of the list.
	        vector.addNeighbor("0");

	        // If wellformed works correctly, after insertion 
	        // it checks and should find the null and fail.
	        fail("Should have caused AssertionError due to null element");
	    } catch (AssertionError e) {
	        // Expected: wellformed() should catch the null element and assert.
	    } catch (NullPointerException npe) {
	        fail("Got NullPointerException instead of AssertionError. The test scenario needs adjusting.");
	    }
	}

    /**
     * INVARIANT CHECK TEST #2:
     * Insert elements in non-sorted order to break the sorting invariant.
     * wellformed() should detect this when we do another operation.
     */
    public void testInvariantNotSorted() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> neighbors = (List<String>)neighborsField.get(vector);

        // Insert out-of-order elements directly
        neighbors.add("Z");
        neighbors.add("A");

        try {
            // Trigger assertion by removing an element (causing wellformed check)
            vector.removeNeighbor("Z");
            fail("Should have caused AssertionError due to sorting invariant break");
        } catch (AssertionError e) {
            // Expected
        }
    }

    /**
     * INVARIANT CHECK TEST #3:
     * Insert duplicate elements directly. wellformed() should catch this.
     */
    public void testInvariantDuplicates() throws Exception {
        @SuppressWarnings("unchecked")
        List<String> neighbors = (List<String>)neighborsField.get(vector);

        neighbors.add("A");
        neighbors.add("A"); // duplicate

        try {
            // Any operation that checks invariants
            vector.addNeighbor("B");
            fail("Should have caused AssertionError due to duplicate elements");
        } catch (AssertionError e) {
            // Expected
        }
    }

    /**
     * INVARIANT CHECK TEST #4:
     * Confirm normal operations do not cause any AssertionError.
     * This ensures that if we don't break invariants artificially,
     * everything works fine.
     */
    public void testInvariantPreservedUnderNormalUse() {
        // Normal usage: add elements in sorted order, no duplicates, no nulls
        vector.addNeighbor("A");
        vector.addNeighbor("B");
        vector.addNeighbor("C");
        // Should not fail
        assertEquals(3, vector.size());
    }
}
