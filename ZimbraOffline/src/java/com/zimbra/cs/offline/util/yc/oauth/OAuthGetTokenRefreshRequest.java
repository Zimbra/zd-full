/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

public class OAuthGetTokenRefreshRequest extends OAuthGetTokenRequest {

    public OAuthGetTokenRefreshRequest(OAuthToken token) {
        super(token, null);
    }

    @Override
    protected void doFillSpecificParams() {
        super.doFillSpecificParams();
        this.removeParam(OAuthConstants.OAUTH_VERIFIER);
        this.addParam(OAuthConstants.OAUTH_SESSION_HANDLE, getToken().getSessionHandle());
    }

    @Override
    protected String getVerifier() {
        return "n";
    }

    @Override
    protected String getStep() {
        return "(step 4, refresh)";
    }
}
