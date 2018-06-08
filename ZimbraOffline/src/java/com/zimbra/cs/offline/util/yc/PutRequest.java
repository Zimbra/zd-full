/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.yc.oauth.OAuthPutContactRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthToken;

public class PutRequest extends Request {

    private String reqBody;

    public PutRequest(OAuthToken token, String reqBody) {
        super(token);
        this.reqBody = reqBody;
    }

    @Override
    public PutResponse send() throws YContactException {
        OAuthRequest req = new OAuthPutContactRequest(this.getToken(), this.reqBody);
        String resp = req.send();

        OfflineLog.yab.debug(resp);

        return new PutResponse(200, resp);
    }
}
