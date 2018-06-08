/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import com.google.gdata.client.authn.oauth.OAuthUtil;

/**
 * step 1 of OAuth
 *
 */
public class OAuthGetRequestTokenRequest extends OAuthRequest {

    public OAuthGetRequestTokenRequest(OAuthToken token) {
        super(token);
    }
    
    @Override
    protected String getEndpointURL() {
        return OAuthConstants.OAUTH_GET_REQ_TOKEN_URL;
    }

    @Override
    protected void doFillSpecificParams() {
        this.addParam(OAuthConstants.OAUTH_CALLBACK, "oob");
        this.addParam(OAuthConstants.OAUTH_CONSUMER_KEY, OAuthConstants.OAUTH_CONSUMER_KEY_VALUE);
        this.addParam(OAuthConstants.OAUTH_NONCE, OAuthUtil.getNonce());
        this.addParam(OAuthConstants.OAUTH_SIGNATURE_METHOD, OAuthHelper.getSignatureMethod());
        this.addParam(OAuthConstants.OAUTH_TIMESTAMP, OAuthUtil.getTimestamp());
        this.addParam(OAuthConstants.OAUTH_VERSION, "1.0");
    }

    @Override
    protected String getStep() {
        return "(step 1)";
    }

    @Override
    protected String getHttpMethod() {
        return OAuthConstants.POST_METHOD;
    }
}
