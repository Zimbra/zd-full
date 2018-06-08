/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

public class OAuthGetTokenTest {

    @Test
    public void getAccessTokenTest() {
        try {
            OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
            String resp = req.send();
            OAuthResponse response = new OAuthGetRequestTokenResponse(resp);
            System.out.println("paste it into browser and input the highlighted codes below: "
                    + response.getToken().getNextUrl());

            System.out.print("Verifier: ");
            Scanner scan = new Scanner(System.in);
            String verifier = scan.nextLine();
            req = new OAuthGetTokenRequest(response.getToken(), verifier);
            resp = req.send();
            response = new OAuthGetTokenResponse(resp);

            Assert.assertNotNull(response.getToken().getToken());
            Assert.assertNotNull(response.getToken().getTokenSecret());
            Assert.assertNotNull(response.getToken().getSessionHandle());
            Assert.assertNotNull(response.getToken().getGuid());

        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void refreshToken() {
        try {
            OAuthRequest req = new OAuthGetRequestTokenRequest(new OAuthToken());
            String resp = req.send();
            OAuthResponse response = new OAuthGetRequestTokenResponse(resp);
            System.out.println("again, paste the highlighted codes below: " + response.getToken().getNextUrl());

            System.out.print("Verifier: ");
            Scanner scan = new Scanner(System.in);
            String verifier = scan.nextLine();
            req = new OAuthGetTokenRequest(response.getToken(), verifier);
            resp = req.send();
            response = new OAuthGetTokenResponse(resp);

            try {
                req = new MockOAuthGetTokenRequest(response.getToken());
                resp = req.send();
                response = new OAuthGetTokenResponse(resp);
            } catch (Exception e) {
                OAuthRequest refreshReq = new OAuthGetTokenRefreshRequest(response.getToken());
                OAuthResponse refreshResp = new OAuthGetTokenResponse(refreshReq.send());
                Assert.assertNotNull(refreshResp.getToken().getToken());
                Assert.assertNotNull(refreshResp.getToken().getTokenSecret());
                Assert.assertNotNull(refreshResp.getToken().getSessionHandle());
                Assert.assertNotNull(refreshResp.getToken().getGuid());
                return;
            }

            Assert.fail("expecting 401 returns for expired token");

        } catch (Exception e) {
            Assert.fail();
        }
    }

    private class MockOAuthGetTokenRequest extends OAuthGetTokenRequest {

        public MockOAuthGetTokenRequest(OAuthToken token) {
            super(token, null);
        }

        @Override
        protected void doFillSpecificParams() {
            super.doFillSpecificParams();
            addParam(OAuthConstants.OAUTH_TIMESTAMP, "" + (System.currentTimeMillis() / 1000 - 7200)); // expires
                                                                                                       // in
                                                                                                       // 3600
                                                                                                       // sec
        }

        @Override
        protected String getVerifier() {
            return "notnull";
        }
    }
}
