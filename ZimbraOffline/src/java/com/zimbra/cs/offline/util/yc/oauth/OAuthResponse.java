/*
 * 
 */
package com.zimbra.cs.offline.util.yc.oauth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.cs.offline.util.yc.YContactException;

public abstract class OAuthResponse {

    private String rawResp;
    private OAuthToken token;
    
    public OAuthResponse(String resp) throws YContactException {
        this.rawResp = resp;
        handleResponse();
    }
    
    protected abstract void handleResponse() throws YContactException;

    protected String getRawResponse() {
        return this.rawResp;
    }
    
    public OAuthToken getToken() {
        return this.token;
    }
    
    public void setToken(OAuthToken token) {
        this.token = token;
    }
    
    protected String getByKey(String key) {
        Pattern p = Pattern.compile(key+"=([^&]+)");
        Matcher matcher = p.matcher(this.rawResp);
        if (matcher.find() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return "";
    }
    
    public String toString() {
        return this.rawResp;
    }
}
