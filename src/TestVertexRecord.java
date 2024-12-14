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
    
    public void test100Constructor() {
        
    }
    
    // ----- Tests of getAdjacencySetSize ----- //
    
    public void test200GetAdjacencySetSize() {
        // 
    }
    
    // ----- Tests of getLogicalId ----- //
    
    public void test300GetLogicalId() {
        // 
    }
    
    // ----- Tests of setLogicalId ----- //
    
    public void test400SetLogicalId() {
        // 
    }
}
