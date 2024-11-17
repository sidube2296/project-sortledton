package test.sortledtongraph;

import org.junit.Test;
import org.junit.Assert;

import edu.uwm.cs351.SortledtonGraph;


public class TestEdges {

    // ========== Tests of insertEdge() ==========

    @Test
    public void testInsertEdge01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        try {
            sortledtonGraph.insertEdge(123, 456);
        } catch (Exception e) {
            Assert.fail("Exception was thrown: " + e.getMessage());
        }
    }

    // ========== Tests of deleteEdge() ==========

    @Test
    public void testDeleteEdge01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        try {
            sortledtonGraph.insertEdge(123, 456);
        } catch (Exception e) {
            Assert.fail("Exception was thrown: " + e.getMessage());
        }
    }

    // ========== Tests of findEdge() ==========

    @Test
    public void testFindEdge01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        boolean result = sortledtonGraph.findEdge(123, 456);

        Assert.assertEquals(result, true);
    }
}
