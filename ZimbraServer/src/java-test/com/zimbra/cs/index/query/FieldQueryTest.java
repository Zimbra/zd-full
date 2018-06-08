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
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MockMailboxManager;

/**
 * Unit test for {@link FieldQuery}.
 *
 * @author ysasaki
 */
public class FieldQueryTest {
    private static Mailbox mailbox;

    @BeforeClass
    public static void init() throws Exception {
        MockProvisioning prov = new MockProvisioning();
        prov.createAccount("zero@zimbra.com", "secret",
                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0"));
        Provisioning.setInstance(prov);
        mailbox = new MockMailboxManager().getMailboxByAccountId("0");
    }

    @Test
    public void textFieldQuery() throws Exception {
        Query query = FieldQuery.newQuery(mailbox, "company", "zimbra");
        Assert.assertEquals("Q(l.field,company:zimbra)", query.toString());
    }

    @Test
    public void numericFieldQuery() throws Exception {
        Query query = FieldQuery.newQuery(mailbox, "capacity", "3");
        Assert.assertEquals("Q(#capacity#:3)", query.toString());

        query = FieldQuery.newQuery(mailbox, "capacity", ">3");
        Assert.assertEquals("Q(#capacity#:>3)", query.toString());

        query = FieldQuery.newQuery(mailbox, "capacity", ">=3");
        Assert.assertEquals("Q(#capacity#:>=3)", query.toString());

        query = FieldQuery.newQuery(mailbox, "capacity", "<-3");
        Assert.assertEquals("Q(#capacity#:<-3)", query.toString());

        query = FieldQuery.newQuery(mailbox, "capacity", "<=-3");
        Assert.assertEquals("Q(#capacity#:<=-3)", query.toString());
    }

}
