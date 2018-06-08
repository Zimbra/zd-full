/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

public class TestInternetCPID {
    
    @Test
    public void testInternetCPID() {
        Assert.assertTrue(InternetCPID.getInternetCPID("US-ASCII") == 20127);
        Assert.assertTrue(InternetCPID.getInternetCPID("IBM037") == 37);
        Assert.assertTrue(InternetCPID.getInternetCPID("ISO-8859-1") == 28591);
        Assert.assertTrue(InternetCPID.getInternetCPID("windows-1250") == 1250);
        Assert.assertTrue(InternetCPID.getInternetCPID("x-mac-romanian") == 10010);
        
        //should be case insensitive
        Assert.assertTrue(InternetCPID.getInternetCPID("us-ascii") == 20127);
        Assert.assertTrue(InternetCPID.getInternetCPID("ibm037") == 37);
        Assert.assertTrue(InternetCPID.getInternetCPID("iso-8859-1") == 28591);
        Assert.assertTrue(InternetCPID.getInternetCPID("utf-8") == 65001);
        
        //Charset unsupported but defined by .NET 4 framework text encoding must return the mapped cpid
        Assert.assertTrue(InternetCPID.getInternetCPID("x-IA5-Swedish") == 20107);
        Assert.assertTrue(InternetCPID.getInternetCPID("x-iscii-de") == 57002);
        Assert.assertTrue(InternetCPID.getInternetCPID("IBM00924") == 20924);
        
        //invalid charset
        Assert.assertTrue(InternetCPID.getInternetCPID("us-zimbra") == -1);
        
        //Charset by alias
        Assert.assertTrue(InternetCPID.getInternetCPID("cp367") == 20127);
        Assert.assertTrue(InternetCPID.getInternetCPID("iso_646.irv:1983") == 20127);
        Assert.assertTrue(InternetCPID.getInternetCPID("ANSI_X3.4-1968") == 20127);
        
        Assert.assertTrue(InternetCPID.getInternetCPID("csISOLatin1") == 28591);
        Assert.assertTrue(InternetCPID.getInternetCPID("IBM819") == 28591);
        Assert.assertTrue(InternetCPID.getInternetCPID("iso-ir-100") == 28591);
    }
    
    @Test
    public void testCharsetEquality() throws Exception {
        Charset charset1 = Charset.forName("us-ascii");
        Charset charset2 = Charset.forName("cp367"); //alias for "US-ASCII"
        Assert.assertTrue(charset1.equals(charset2));
        
        charset2 = Charset.forName("UTF-8");
        Assert.assertFalse(charset1.equals(charset2)); 
    }

}
