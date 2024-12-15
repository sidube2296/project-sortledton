import junit.framework.TestCase;
import edu.uwm.cs351.Neighborhood;
import edu.uwm.cs351.UnrolledSkipList;
import edu.uwm.cs351.VertexRecord;
import edu.uwm.cs351.VertexRecord.WellFormedError;

public class TestVertexRecord extends TestCase {
    
    // ----- Mock Data ----- //
    
    private Neighborhood<Integer> mockAdjacencySet = new UnrolledSkipList<Integer>();
    
    // ----- Tests of wellFormed ----- //
    
    // logicalId is invalid
    public void test001WellFormed() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, -1, 0);
        
        spy.setReporter((WellFormedError message) -> assertEquals(message, WellFormedError.LOGICAL_ID));
        assertFalse( spy.wellFormed(instance) );
    }
    
    // adjacencySet is invalid
    public void test002WellFormed() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = spy.newInstance(null, 0, 0);
        
        spy.setReporter((WellFormedError message) -> assertEquals(message, WellFormedError.ADJACENCY_SET));
        assertFalse( spy.wellFormed(instance) );
    }
    
    // adjacencySetSize is invalid
    public void test003WellFormed() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, 0, -1);
        
        spy.setReporter((WellFormedError message) -> assertEquals(message, WellFormedError.ADJACENCY_SET_SIZE));
        assertFalse( spy.wellFormed(instance) );
    }
    
    // adjacencySetSize is incorrect
    public void test004WellFormed() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, 0, 5);
        
        spy.setReporter((WellFormedError message) -> assertEquals(message, WellFormedError.ADJACENCY_SET_MISMATCH));
        assertFalse( spy.wellFormed(instance) );
    }
    
    // All fields are valid
    public void test005WellFormed() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, 0, 0);

        assertTrue( spy.wellFormed(instance) );
    }

    // ----- Tests of Constructors ----- //
    
    // Constructor without arguments
    public void test100Constructor() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = new VertexRecord<Integer>();
        
        assertTrue( spy.wellFormed(instance) );
        assertEquals( instance.adjacencySetSize, 0 );
        assertEquals( instance.adjacencySet.getClass().getSimpleName(), "PowerofTwo" );
    }
    
    // Constructor with arguments
    public void test101Constructor() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        VertexRecord<Integer> instance = new VertexRecord<Integer>(1234, mockAdjacencySet);
        
        assertTrue( spy.wellFormed(instance) );
        assertEquals( instance.logicalId, 1234 );
        assertEquals( instance.adjacencySet, mockAdjacencySet );
        assertEquals( instance.adjacencySetSize, 0 );
    }
    
    // ----- Tests of getAdjacencySetSize ----- //
    
    public void test200GetAdjacencySetSize() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        
        Neighborhood<Integer> mockSkiplist = new UnrolledSkipList<Integer>();
        mockSkiplist.addNeighbor(1111);
        mockSkiplist.addNeighbor(1112);
        
        VertexRecord<Integer> instance = spy.newInstance(mockSkiplist, 1110, 2);
        
        assertTrue( spy.wellFormed(instance) );
        assertEquals( instance.getAdjacencySetSize(), 2 );
    }
    
    // ----- Tests of getLogicalId ----- //
    
    public void test300GetLogicalId() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, 1110, 0);
        
        assertTrue( spy.wellFormed(instance) );
        assertEquals( instance.getLogicalId(), 1110 );
    }
    
    // ----- Tests of setLogicalId ----- //
    
    public void test400SetLogicalId() {
        VertexRecord.Spy spy = new VertexRecord.Spy();
        
        VertexRecord<Integer> instance = spy.newInstance(mockAdjacencySet, 1110, 0);
        
        assertTrue( spy.wellFormed(instance) );
        assertEquals( instance.getLogicalId(), 1110 );
        
        instance.setLogicalId( 2222 );
        
        assertEquals( instance.getLogicalId(), 2222 );
    }
}
