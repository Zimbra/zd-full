/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import com.google.gdata.client.authn.oauth.OAuthUtil;

/**
 * step 3 of OAuth
 * 
 */
public class OAuthGetTokenRequest extends OAuthRequest {

    private String verifier = "";
    
    public OAuthGetTokenRequest(OAuthToken token, String verifier) {
        super(token);
        this.verifier = verifier;
    }

    @Override
    protected String getEndpointURL() {
        return OAuthConstants.OAUTH_GET_TOKEN_URL;
    }

    @Override
    protected void doFillSpecificParams() {
        this.addParam(OAuthConstants.OAUTH_CONSUMER_KEY, OAuthConstants.OAUTH_CONSUMER_KEY_VALUE);
        this.addParam(OAuthConstants.OAUTH_NONCE, OAuthUtil.getNonce());
        this.addParam(OAuthConstants.OAUTH_SIGNATURE_METHOD, OAuthHelper.getSignatureMethod());
        this.addParam(OAuthConstants.OAUTH_TIMESTAMP, OAuthUtil.getTimestamp());
        this.addParam(OAuthConstants.OAUTH_VERSION, "1.0");
        this.addParam(OAuthConstants.OAUTH_TOKEN, getToken().getToken());
        this.addParam(OAuthConstants.OAUTH_VERIFIER, getVerifier());
    }

    @Override
    protected String getVerifier() {
        return this.verifier;
    }

    @Override
    protected String getStep() {
        return "(step 3)";
    }

    @Override
    protected String getHttpMethod() {
        return OAuthConstants.POST_METHOD;
    }
}
