/*
 * 
 */
package com.zimbra.cs.service;

import org.junit.Assert;
import org.junit.Test;

public class FeedManagerTest {
    @Test
    public void subject() throws Exception {
        Assert.assertEquals("null", "", FeedManager.parseTitle(null));
        Assert.assertEquals("no transform", "test subject", FeedManager.parseTitle("test subject"));
        Assert.assertEquals("link", "test subject test", FeedManager.parseTitle("test <a>subject</a> test"));
        Assert.assertEquals("embed link", "test subject", FeedManager.parseTitle("test su<a>bject</a>"));
        Assert.assertEquals("bold", "test subject test", FeedManager.parseTitle("test <b>subject</b> test"));
        Assert.assertEquals("break", "test subject", FeedManager.parseTitle("test<br>subject"));
        Assert.assertEquals("space break", "test subject", FeedManager.parseTitle("test <br>subject"));
    }
}
