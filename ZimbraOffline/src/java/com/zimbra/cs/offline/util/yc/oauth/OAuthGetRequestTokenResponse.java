/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import com.zimbra.cs.offline.util.yc.YContactException;


public class OAuthGetRequestTokenResponse extends OAuthResponse {

    public OAuthGetRequestTokenResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() {
        String token = getByKey(OAuthConstants.OAUTH_TOKEN);
        String tokenSecret = getByKey(OAuthConstants.OAUTH_TOKEN_SECRET);
        String url = getByKey(OAuthConstants.OAUTH_REQUEST_AUTH_URL);
        OAuthToken otoken = new OAuthToken(token, tokenSecret);
        otoken.setNextUrl(url);
        this.setToken(otoken);
    }

}
