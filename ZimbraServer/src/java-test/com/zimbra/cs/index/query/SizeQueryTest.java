/*
 * 
 */
package com.zimbra.cs.index.query;

import java.text.ParseException;
import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.zimbra.cs.account.MockProvisioning;
import com.zimbra.cs.account.Provisioning;

/**
 * Unit test for {@link SizeQuery}.
 *
 * @author ysasaki
 */
public class SizeQueryTest {

    @BeforeClass
    public static void init() throws Exception {
        MockProvisioning prov = new MockProvisioning();
        prov.createAccount("zero@zimbra.com", "secret",
                Collections.<String, Object>singletonMap(Provisioning.A_zimbraId, "0-0-0"));
        Provisioning.setInstance(prov);
    }

    @Test
    public void parseSize() throws Exception {
        SizeQuery query = new SizeQuery(SizeQuery.Type.EQ, "1KB");
        Assert.assertEquals("Q(SIZE=1024)", query.toString());

        query = new SizeQuery(SizeQuery.Type.EQ, ">1KB");
        Assert.assertEquals("Q(SIZE>1024)", query.toString());

        query = new SizeQuery(SizeQuery.Type.EQ, "<1KB");
        Assert.assertEquals("Q(SIZE<1024)", query.toString());

        query = new SizeQuery(SizeQuery.Type.EQ, ">=1KB");
        Assert.assertEquals("Q(SIZE>1023)", query.toString());

        query = new SizeQuery(SizeQuery.Type.EQ, "<=1KB");
        Assert.assertEquals("Q(SIZE<1025)", query.toString());

        query = new SizeQuery(SizeQuery.Type.EQ, "1 KB");
        Assert.assertEquals("Q(SIZE=1024)", query.toString());

        try {
            query = new SizeQuery(SizeQuery.Type.EQ, "x KB");
            Assert.fail();
        } catch (ParseException expected) {
        }
    }

}
