/*
 * 
 */
package com.zimbra.cs.index.analysis;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;

import org.apache.lucene.analysis.Tokenizer;
import org.junit.Assert;
import org.junit.Test;

import com.zimbra.cs.index.ZimbraAnalyzerTest;

/**
 * Unit test for {@link AddrCharTokenizer}.
 *
 * @author ysasaki
 */
public class AddrCharTokenizerTest {

    @Test
    public void addrCharTokenizer() throws Exception {
        Tokenizer tokenizer = new AddrCharTokenizer(new StringReader("all-snv"));
        Assert.assertEquals(Collections.singletonList("all-snv"), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader("."));
        Assert.assertEquals(Collections.singletonList("."), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader(".. ."));
        Assert.assertEquals(Arrays.asList("..", "."), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader(".abc"));
        Assert.assertEquals(Collections.singletonList(".abc"), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader("a"));
        Assert.assertEquals(Collections.singletonList("a"), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader("test.com"));
        Assert.assertEquals(Collections.singletonList("test.com"), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader("user1@zim"));
        Assert.assertEquals(Collections.singletonList("user1@zim"), ZimbraAnalyzerTest.toTokens(tokenizer));

        tokenizer.reset(new StringReader("user1@zimbra.com"));
        Assert.assertEquals(Collections.singletonList("user1@zimbra.com"), ZimbraAnalyzerTest.toTokens(tokenizer));
    }

    @Test
    public void japanese() throws Exception {
        Tokenizer tokenizer = new AddrCharTokenizer(new StringReader("\u68ee\u3000\u6b21\u90ce"));
        Assert.assertEquals(Arrays.asList("\u68ee", "\u6b21\u90ce"), ZimbraAnalyzerTest.toTokens(tokenizer));
    }

}
