/*
 * 
 */

package com.zimbra.cs.index;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link MultiQueryResults}.
 *
 * @author ysasaki
 */
public class MultiQueryResultsTest {

    @Test
    public void multi() throws Exception {
        SortBy sort = SortBy.DATE_DESCENDING;
        MultiQueryResults multi = new MultiQueryResults(3, sort);

        MockQueryResults result = new MockQueryResults(sort);
        MockHit hit = new MockHit(1, "1-1");
        hit.setDate(10);
        result.add(hit);
        hit = new MockHit(2, "1-2");
        hit.setDate(8);
        result.add(hit);
        hit = new MockHit(2, "1-3");
        hit.setDate(6);
        result.add(hit);
        multi.add(result);

        result = new MockQueryResults(SortBy.DATE_DESCENDING);
        hit = new MockHit(1, "2-1");
        hit.setDate(9);
        result.add(hit);
        hit = new MockHit(2, "2-2");
        hit.setDate(7);
        result.add(hit);
        hit = new MockHit(2, "2-3");
        hit.setDate(5);
        result.add(hit);
        multi.add(result);

        Assert.assertEquals("1-1", multi.getNext().getName());
        Assert.assertEquals("2-1", multi.getNext().getName());
        Assert.assertEquals("1-2", multi.getNext().getName());
        Assert.assertFalse(multi.hasNext());
        Assert.assertNull(multi.getNext());

        multi.shrink(1);
        multi.resetIterator();
        Assert.assertEquals("2-1", multi.getNext().getName());
        Assert.assertEquals("1-2", multi.getNext().getName());
        Assert.assertFalse(multi.hasNext());
        Assert.assertNull(multi.getNext());
    }

}
