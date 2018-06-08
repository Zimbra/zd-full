/*
 * 
 */
package com.zimbra.zimbrasync.wbxml;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 * ActiveSync multipart utilities unit tests
 * @author smukhopadhyay
 *
 */
public class TestMultiPartUtil {
    
    @Test
    public void testIntToInverseByteArrayConversions() throws Exception {
        testIntToInverseByteArray(2);
        testIntToInverseByteArray(17);
        testIntToInverseByteArray(1024);
        testIntToInverseByteArray(2048);
    }
    
    private void testIntToInverseByteArray(int i) throws IOException {
        byte[] inverseIntBytes = MultiPartUtil.intToInverseByteArray(i);
        int j = MultiPartUtil.inverseByteArrayToInt(inverseIntBytes);
        Assert.assertTrue(i == j);
    }
}
