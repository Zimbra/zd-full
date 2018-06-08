/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import com.zimbra.cs.offline.util.yc.oauth.OAuthToken;

public abstract class Request {

    private OAuthToken token;

    public Request(OAuthToken token) {
        this.token = token;
    }

    protected OAuthToken getToken() {
        return this.token;
    }

    abstract Response send() throws YContactException;
}
