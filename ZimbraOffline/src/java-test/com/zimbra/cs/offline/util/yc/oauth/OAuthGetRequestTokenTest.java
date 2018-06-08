/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.Test;

import com.zimbra.common.service.ServiceException;

public class OAuthGetRequestTokenTest {

    static {
        BasicConfigurator.configure();
    }

    @Test
    public void getRequestTokenTest() {
        OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
        String respStr = null;
        try {
            respStr = req.send();
        } catch (Exception e) {
            Assert.fail("OAuth get request token failed");
        }
        Assert.assertNotNull("failed to get response", respStr);
        OAuthResponse resp = null;
        try {
            resp = new OAuthGetRequestTokenResponse(respStr);
        } catch (ServiceException e) {
            Assert.fail("OAuth create response failed");
        }
        Assert.assertNotNull("failed to create OAuthResponse", resp);
        Assert.assertNotNull(resp.getToken().getToken());
        Assert.assertNotNull(resp.getToken().getTokenSecret());
    }

    public static void main(String[] args) {
        OAuthGetRequestTokenTest test = new OAuthGetRequestTokenTest();
        test.getRequestTokenTest();
    }
}
