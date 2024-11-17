package test.sortledtongraph;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import edu.uwm.cs351.SortledtonGraph;
import edu.uwm.cs351.SortledtonGraph.Spy;
import edu.uwm.cs351.SortledtonGraph.Spy.VertexEntry;

public class TestInternals {

    // ========== Tests of SortledtonGraph() ==========

    @Test
    public void testSortledtonGraph01() {
        SortledtonGraph<Integer> instance = Spy.newInstance(0, new HashMap<Integer, Integer>(0,1), new VertexEntry[0]);

        boolean isWellFormed = Spy.wellFormed(instance);

        Assert.assertTrue(isWellFormed);
    }


    // ========== Tests of wellFormed() ==========

    // TODO
}
