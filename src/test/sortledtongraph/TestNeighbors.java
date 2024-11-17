package test.sortledtongraph;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.uwm.cs351.SortledtonGraph;

public class TestNeighbors {

    // ========== Tests of getNeighbors() ==========

    @Test
    public void testGetNeighbors01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        List<Integer> result = sortledtonGraph.getNeighbors(0);

        Assert.assertEquals(result, null);
    }


    // ========== Tests of intersectNeighbors() ==========

    @Test
    public void testIntersectNeighbors01() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        List<Integer> result = sortledtonGraph.intersectNeighbors(123, 456);

        Assert.assertEquals(result, null);
    }
}
