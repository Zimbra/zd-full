/*
 * 
 */
package com.zimbra.qa.unittest;

import junit.framework.TestCase;

import com.zimbra.cs.account.AttributeManager;


public class TestBuildInfo extends TestCase {

    public void testInVersion() throws Exception {
        AttributeManager am = AttributeManager.getInstance();

        assertTrue(am.inVersion("zimbraId", "0"));
        assertTrue(am.inVersion("zimbraId", "5.0.10"));

        assertFalse(am.inVersion("zimbraZimletDomainAvailableZimlets", "5.0.9"));
        assertTrue(am.inVersion("zimbraZimletDomainAvailableZimlets", "5.0.10"));
        assertTrue(am.inVersion("zimbraZimletDomainAvailableZimlets", "5.0.11"));
        assertTrue(am.inVersion("zimbraZimletDomainAvailableZimlets", "5.5"));
        assertTrue(am.inVersion("zimbraZimletDomainAvailableZimlets", "6"));
    }

    public static void main(String[] args) throws Exception  {
        TestUtil.runTest(TestBuildInfo.class);
    }

}
