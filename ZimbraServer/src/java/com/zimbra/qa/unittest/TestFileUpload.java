/*
 * 
 */
package com.zimbra.qa.unittest;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.cs.zclient.ZMailbox;

public class TestFileUpload
extends TestCase {
    
    private static final String USER_NAME = "user1";
    private static final String NAME_PREFIX = TestFileUpload.class.getSimpleName();
    
    public void setUp()
    throws Exception {
        cleanUp();
    }

    /**
     * Confirms that &lt;script&gt; tags are JavaScript-encoded when passed
     * to the <tt>requestId</tt> parameter.  See bug 40377.
     * @throws Exception
     */
    public void testRequestId()
    throws Exception {
        ZMailbox mbox = TestUtil.getZMailbox(USER_NAME);
        URI uri = mbox.getUploadURI();
        HttpClient client = mbox.getHttpClient(uri);
        
        Part attachmentPart = mbox.createAttachmentPart("test.txt", new byte[10]);
        Part requestIdPart = new StringPart("requestId", "<script></script>");
        Part[] parts = new Part[] { attachmentPart, requestIdPart };
        
        PostMethod post = new PostMethod(uri.toString());
        post.setRequestEntity( new MultipartRequestEntity(parts, post.getParams()) );
        int statusCode = HttpClientUtil.executeMethod(client, post);
        assertEquals(200, statusCode);
        
        String response = post.getResponseBodyAsString();
        assertTrue("Response does not contain 'script': " + response, response.contains("script"));
        assertFalse("Response contains '<script>': " + response, response.contains("<script>"));
        
        post.releaseConnection();
    }
    
    public void tearDown()
    throws Exception {
        cleanUp();
    }
    
    private void cleanUp()
    throws Exception {
        TestUtil.deleteTestData(USER_NAME, NAME_PREFIX);
    }

    public static void main(String[] args)
    throws Exception {
        TestUtil.cliSetup();
        TestUtil.runTest(TestFileUpload.class);
    }
}
