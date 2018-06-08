/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import com.google.gdata.client.authn.oauth.OAuthUtil;
import com.zimbra.cs.offline.OfflineLog;

public class OAuthGetContactsRequest extends OAuthRequest {

    private int clientRev = 0;
    
    public OAuthGetContactsRequest(OAuthToken token, int clientRev) {
        super(token);
        this.clientRev = clientRev;
    }

    @Override
    protected String getEndpointURL() {
        String url = String.format(OAuthConstants.OAUTH_GET_CONTACTS_URL, getToken().getGuid(), "" + this.clientRev);
        OfflineLog.yab.debug("get contacts url: %s", url);
        return url;
    }

    @Override
    protected void doFillSpecificParams() {
        this.addParam(OAuthConstants.OAUTH_CONSUMER_KEY, OAuthConstants.OAUTH_CONSUMER_KEY_VALUE);
        this.addParam(OAuthConstants.OAUTH_NONCE, OAuthUtil.getNonce());
        this.addParam(OAuthConstants.OAUTH_SIGNATURE_METHOD, OAuthHelper.getSignatureMethod());
        this.addParam(OAuthConstants.OAUTH_TIMESTAMP, OAuthUtil.getTimestamp());
        this.addParam(OAuthConstants.OAUTH_VERSION, "1.0");
        this.addParam(OAuthConstants.OAUTH_TOKEN, getToken().getToken());
    }

    @Override
    protected String getStep() {
        return "(step 5)";
    }

    @Override
    protected String getHttpMethod() {
        return OAuthConstants.GET_METHOD;
    }
}
