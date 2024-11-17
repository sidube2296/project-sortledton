package test.sortledtongraph;

import org.junit.Test;
import org.junit.Assert;

import edu.uwm.cs351.SortledtonGraph;

public class TestVertices {

    // ========== Tests of deleteVertex() ==========

    // TODO

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

    }

    // ========== Tests of insertVertex() ==========

    // TODO
}
