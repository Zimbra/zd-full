/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.util.yc.oauth.OAuthGetContactsRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthRequest;
import com.zimbra.cs.offline.util.yc.oauth.OAuthToken;

public class SyncRequest extends Request {

    private int reqRev = 0;

    public SyncRequest(OAuthToken token) {
        super(token);
    }

    public SyncRequest(OAuthToken token, int rev) {
        super(token);
        this.reqRev = rev;
    }

    @Override
    public SyncResponse send() throws YContactException {
        OAuthRequest req = new OAuthGetContactsRequest(this.getToken(), this.reqRev);
        String resp = req.send();

        OfflineLog.yab.debug(resp);

        return new SyncResponse(200, resp);
    }

}
