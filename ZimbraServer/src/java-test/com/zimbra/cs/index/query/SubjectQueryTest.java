/*
 * 
 */
package com.zimbra.cs.index.query;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.cs.account.MockProvisioning;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.index.ZimbraAnalyzer;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MockMailboxManager;

/**
 * Unit test for {@link SubjectQuery}.
 *
 * @author ysasaki
 */
public class SubjectQueryTest {

    @BeforeClass
    public static void init() throws Exception {
        MockProvisioning prov = new MockProvisioning();
        prov.createAccount("zero@zimbra.com", "secret",
                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0"));
        Provisioning.setInstance(prov);
    }

    @Test
    public void emptySubject() throws Exception {
        Mailbox mbox = new MockMailboxManager().getMailboxByAccountId("0");
        Query query = SubjectQuery.create(mbox,
                ZimbraAnalyzer.getInstance(), "");
        Assert.assertEquals(TextQuery.class, query.getClass());
        Assert.assertEquals("Q(subject)", query.toString());
    }

}
