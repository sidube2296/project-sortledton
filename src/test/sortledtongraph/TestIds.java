package test.sortledtongraph;

import org.junit.Test;
import org.junit.Assert;

import edu.uwm.cs351.SortledtonGraph;


public class TestIds {

    // ========== Tests of logicalId() ==========

    @Test
    public void testLogicalId() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        int result = sortledtonGraph.logicalId(123);

        Assert.assertEquals(result, 0);
    }

    // ========== Tests of physicalId() ==========

    @Test
    public void testPhysicalId() {
        SortledtonGraph<Integer> sortledtonGraph = new SortledtonGraph<Integer>();

        try {
            sortledtonGraph.physicalId(123);
        } catch (NullPointerException e) {
            Assert.assertEquals("Cannot invoke \"java.lang.Integer.intValue()\" because the return value of \"java.util.HashMap.get(Object)\" is null", e.getMessage());
        }
    }
}
