/*
 * 
 */
package com.zimbra.zimbrasync.data;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ProtocolVersion unit tests
 * @author smukhopadhyay
 *
 */
public class TestProtocolVersion {
    private static ProtocolVersion currentVersion;
    private static ProtocolVersion equalToCurrentVersion;
    private static ProtocolVersion previousVersion;
    
    @BeforeClass
    public static void init() {
        currentVersion = new ProtocolVersion("12.0");
        equalToCurrentVersion = new ProtocolVersion("12.0");
        previousVersion = new ProtocolVersion("2.5");
    }
    
    @Test
    public void testProtocolVersion() {
        Assert.assertTrue(currentVersion.getMajor() == 12);
        Assert.assertTrue(currentVersion.getMinor() == 0);
        
        Assert.assertTrue(previousVersion.getMajor() == 2);
        Assert.assertTrue(previousVersion.getMinor() == 5);
        
        Assert.assertTrue(currentVersion.toString().equals("12.0"));
        Assert.assertTrue(previousVersion.toString().equals("2.5"));
    }
    
    @Test
    public void testVersionObjectsEquality() {
        Assert.assertTrue(currentVersion.equals(equalToCurrentVersion));
        Assert.assertFalse(currentVersion.equals(previousVersion));
        
        Assert.assertTrue(currentVersion.hashCode() == equalToCurrentVersion.hashCode());
        Assert.assertFalse(currentVersion.hashCode() == previousVersion.hashCode());
    }
    
    @Test
    public void testProtocolVersionCompliance() {
        Assert.assertTrue(currentVersion.complies(previousVersion));
    }
    
    

}
