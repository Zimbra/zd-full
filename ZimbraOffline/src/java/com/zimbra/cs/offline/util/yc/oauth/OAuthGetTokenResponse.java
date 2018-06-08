/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.zimbra.cs.offline.util.yc.YContactException;

public class OAuthGetTokenResponse extends OAuthResponse {

    public OAuthGetTokenResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() throws YContactException {
        String token;
        try {
            token = URLDecoder.decode(getByKey(OAuthConstants.OAUTH_TOKEN), "UTF-8");
            String tokenSecret = URLDecoder.decode(getByKey(OAuthConstants.OAUTH_TOKEN_SECRET), "UTF-8");
            OAuthToken otoken = new OAuthToken(token, tokenSecret);
            otoken.setSessionHandle(getByKey(OAuthConstants.OAUTH_SESSION_HANDLE));
            otoken.setGuid(getByKey(OAuthConstants.OAUTH_YAHOO_GUID));
            this.setToken(otoken);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthException("error when decoding token", "", false, e, null);
        }
    }
}
