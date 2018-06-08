/*
 * 
 */
package com.zimbra.cs.util.yauth;

import java.io.IOException;

public abstract class TokenStore {
    public String newToken(String appId, String user, String pass)
        throws AuthenticationException, IOException {
        removeToken(appId, user);
        String token = RawAuth.getToken(appId, user, pass);
        putToken(appId, user, token);
        return token;
    }

    public boolean hasToken(String appId, String user) {
        return getToken(appId, user) != null;
    }

    protected abstract void putToken(String appId, String user, String token);
    public abstract String getToken(String appId, String user);
    public abstract void removeToken(String appId, String user);
    public abstract int size();
}
