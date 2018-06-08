/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import junit.framework.TestCase;

public class TestBinaryCodepages extends TestCase {
    public void testCodepageToNamespace()
			throws Exception {
    	String ns = BinaryCodec.codepageToNamespace(0);
    	assertEquals(ns, BinaryCodec.NAMESPACE_AIRSYNC);
    	
    	ns = BinaryCodec.codepageToNamespace(7);
    	assertEquals(ns, BinaryCodec.NAMESPACE_FOLDERHIERARCHY);
    }

    public void testNamespaceToCodepage()
    		throws Exception {
    	int cp = BinaryCodec.namespaceToCodepage(BinaryCodepages.NAMESPACE_AIRSYNC);
    	assertTrue(cp == 0);
    	
    	cp = BinaryCodec.namespaceToCodepage(BinaryCodepages.NAMESPACE_FOLDERHIERARCHY);
    	assertTrue(cp == 7);
    }
    
    public void testCodeToTagName()
			throws Exception {
		String tag = BinaryCodec.codeToTagName(0, 0x05);
		assertEquals(tag, BinaryCodepages.AIRSYNC_SYNC);
		
		tag = BinaryCodec.codeToTagName(7, 0x16);
		assertEquals(tag, BinaryCodepages.FOLDERHIERARCHY_FOLDERSYNC);
	}
    
    public void testTagnameToCode()
    		throws Exception {
		int code = BinaryCodec.tagNameToCode(0, BinaryCodepages.AIRSYNC_SYNC);
		assertTrue(code == 0x05);
		
		code = BinaryCodec.tagNameToCode(7, BinaryCodepages.FOLDERHIERARCHY_FOLDERSYNC);
		assertTrue(code == 0x16);
	}    
}
