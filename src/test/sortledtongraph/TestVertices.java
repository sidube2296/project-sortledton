package test.sortledtongraph;

import org.junit.Test;
import org.junit.Assert;

import edu.uwm.cs351.SortledtonGraph;

public class TestVertices {

    // ========== Tests of deleteVertex() ==========

    @Test
    public void testDeleteVertex01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        int result = sortledtonGraph.getVertexCount();

        Assert.assertEquals(result, 0);
    }

    // ========== Tests of getVertexCount() ==========

    @Test
    public void testGetVertexCount01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        int result = sortledtonGraph.getVertexCount();

        Assert.assertEquals(result, 0);
    }

    // ========== Tests of hasVertex() ==========

    @Test
    public void testHasVertex01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        try {
            sortledtonGraph.deleteVertex(123);
        } catch (Exception e) {
            Assert.fail("Exception was thrown: " + e.getMessage());
        }
    }

    // ========== Tests of insertVertex() ==========


    @Test
    public void testInsertVertex01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        try {
            sortledtonGraph.insertVertex(123);
        } catch (Exception e) {
            Assert.fail("Exception was thrown: " + e.getMessage());
        }
    }
}
