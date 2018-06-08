/*
 * 
 */
package com.zimbra.cs.index;

import org.junit.Assert;
import org.junit.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.service.util.ItemId;

/**
 * Unit test for {@link MsgQueryResults}.
 *
 * @author ysasaki
 */
public class MsgQueryResultsTest {

    @Test
    public void merge() throws Exception {
        MockQueryResults top = new MockQueryResults(SortBy.NONE);
        top.add(new MessageHit(null, null, 1000, null, null));
        top.add(new MessagePartHit(null, null, 1000, null, null));
        top.add(new MessagePartHit(null, null, 1000, null, null));
        top.add(new MessageHit(null, null, 1001, null, null));
        top.add(new MessageHit(null, null, 1001, null, null));
        top.add(new MessagePartHit(null, null, 1001, null, null));
        top.add(new MessagePartHit(null, null, 1001, null, null));
        top.add(new MessageHit(null, null, 1002, null, null));
        top.add(new MessageHit(null, null, 1003, null, null));

        ProxiedHit phit = new ProxiedHit(null, null);
        phit.itemID = new ItemId("A", 1000);
        top.add(phit);

        phit = new ProxiedHit(null, null);
        phit.itemID = new ItemId("B", 1000);
        top.add(phit);

        MsgQueryResults result = new MsgQueryResults(top, null, SortBy.NONE,
                Mailbox.SearchResultMode.NORMAL);

        ZimbraHit hit = result.getNext();
        Assert.assertEquals(hit.getClass(), MessageHit.class);
        Assert.assertEquals(hit.getItemId(), 1000);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), MessageHit.class);
        Assert.assertEquals(hit.getItemId(), 1001);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), MessageHit.class);
        Assert.assertEquals(hit.getItemId(), 1002);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), MessageHit.class);
        Assert.assertEquals(hit.getItemId(), 1003);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), ProxiedHit.class);
        Assert.assertEquals(hit.getItemId(), 1000);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), ProxiedHit.class);
        Assert.assertEquals(hit.getItemId(), 1000);

        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void proxiedHitNotMerged() throws Exception {
        MockQueryResults top = new MockQueryResults(SortBy.NONE);
        top.add(new MessageHit(null, null, 1000, null, null));

        Element el = XMLElement.create(SoapProtocol.Soap12, "hit");
        el.addAttribute(MailConstants.A_ID, 1000);
        top.add(new ProxiedHit(null, el));

        MsgQueryResults result = new MsgQueryResults(top, null, SortBy.NONE,
                Mailbox.SearchResultMode.NORMAL);

        ZimbraHit hit = result.getNext();
        Assert.assertEquals(hit.getClass(), MessageHit.class);
        Assert.assertEquals(hit.getItemId(), 1000);

        hit = result.getNext();
        Assert.assertEquals(hit.getClass(), ProxiedHit.class);
        Assert.assertEquals(hit.getItemId(), 1000);
    }

}
