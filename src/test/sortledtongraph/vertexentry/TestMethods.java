package test.sortledtongraph.vertexentry;

import org.junit.Test;

import edu.uwm.cs351.SortledtonGraph.Spy.VertexEntry;

import org.junit.Assert;

public class TestMethods {
    // ========== Tests of getAdjacencySetSize() ==========

    @Test
    public void testGetAdjacencySetSize01() {
        VertexEntry vertexEntry = new VertexEntry();

        int result = vertexEntry.getAdjacencySetSize();

        Assert.assertEquals(result, 0);
    }

    // ========== Tests of set & getLogicalId() ==========

    @Test
    public void testLogicalId01() {
        VertexEntry vertexEntry = new VertexEntry();

        // Test initial result after construction
        int result1 = vertexEntry.getLogicalId();
        Assert.assertEquals(result1, 0);

        // Set the value
        vertexEntry.setLogicalId(123);

        // Test result after set was called
        int result2 = vertexEntry.getLogicalId();
        Assert.assertEquals(result2, 123);
    }
}
