/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import com.google.gdata.util.common.net.UriEncoder;

public class OAuthToken {

    private String token = "";
    private String tokenSecret = "";
    private String nextUrl = "";
    private String sessionHandle = "-1";
    private String guid = "";
    private long lastAccessTime = 0;

    public OAuthToken() {
    }

    public OAuthToken(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
        this.lastAccessTime = System.currentTimeMillis();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getNextUrl() {
        return nextUrl;
    }

    public void setNextUrl(String nextUrl) {
        this.nextUrl = UriEncoder.decode(nextUrl);
    }

    public String getSessionHandle() {
        return sessionHandle;
    }

    public void setSessionHandle(String sessionHandle) {
        this.sessionHandle = sessionHandle;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() - this.lastAccessTime) > OAuthConstants.OAUTH_TOKEN_EXPIRE_PERIOD;
    }

    public boolean isNew() {
        return "-1".equals(this.token) && "-1".equals(this.tokenSecret);
    }

    public static OAuthToken newToken() {
        return new OAuthToken("-1", "-1");
    }

    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append(this.token).append(",").append(this.tokenSecret).append(",").append(this.sessionHandle).append(",")
                .append(this.lastAccessTime);
        return buff.toString();
    }
}
