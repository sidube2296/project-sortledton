package test.sortledtongraph.vertexentry;

import org.junit.Assert;
import org.junit.Test;

import edu.uwm.cs351.SortledtonGraph.Spy;

public class TestInternals {

    // ========== Tests of VetrexEntry() ==========

    @Test
    public void testVertexEntry01() {
        try {
            new Spy.VertexEntry();
        } catch (Exception e) {
            Assert.fail("Exception was thrown: " + e.getMessage());
        }
    }

    // ========== Tests of wellFormed() ==========

    // TODO
}
