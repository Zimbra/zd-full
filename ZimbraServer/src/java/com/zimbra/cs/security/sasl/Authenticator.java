/*
 * 
 */

package com.zimbra.cs.security.sasl;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.auth.AuthContext;

import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.security.sasl.SaslServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Authenticator {
    static interface AuthenticatorFactory {
        public Authenticator getAuthenticator(AuthenticatorUser authUser);
    }

    private static final Map<String, AuthenticatorFactory> mRegisteredMechanisms = new LinkedHashMap<String, AuthenticatorFactory>();
    private static Collection<String> mMechanismList = Collections.emptyList();

    static {
        registerMechanism(PlainAuthenticator.MECHANISM, new AuthenticatorFactory() {
            public Authenticator getAuthenticator(AuthenticatorUser authUser)  { return new PlainAuthenticator(authUser); }
        });
        registerMechanism(GssAuthenticator.MECHANISM, new AuthenticatorFactory() {
            public Authenticator getAuthenticator(AuthenticatorUser authUser)  { return new GssAuthenticator(authUser); }
        });
        registerMechanism(ZimbraAuthenticator.MECHANISM, new AuthenticatorFactory() {
            public Authenticator getAuthenticator(AuthenticatorUser authUser)  { return new ZimbraAuthenticator(authUser); }
        });
    }

    public static void registerMechanism(String mechanism, AuthenticatorFactory authFactory) {
        mRegisteredMechanisms.put(mechanism.toUpperCase(), authFactory);
        mMechanismList = Collections.unmodifiableCollection(mRegisteredMechanisms.keySet());
    }

    public static Authenticator getAuthenticator(String mechanism, AuthenticatorUser authUser) {
        AuthenticatorFactory authFactory = mRegisteredMechanisms.get(mechanism.toUpperCase());
        if (authFactory == null)
            return null;
        Authenticator auth = authFactory.getAuthenticator(authUser);
        return auth.isSupported() ? auth : null;
    }

    public static Collection<String> listMechanisms() {
        return mMechanismList;
    }


    protected final String mProtocol;
    protected final String mMechanism;
    protected final AuthenticatorUser mAuthUser;
    protected boolean mComplete;
    protected boolean mAuthenticated;
    protected Socket mConnection;

    protected Authenticator(String mechanism, AuthenticatorUser authUser) {
        mProtocol  = authUser.getProtocol();
        mMechanism = mechanism;
        mAuthUser  = authUser;
    }

    /** Whether this Authenticator is valid for the server/protocol/etc. as
     *  constrained by its AuthenticatorUser. */
    protected abstract boolean isSupported();

    public abstract boolean initialize() throws IOException;

    public abstract void handle(byte[] data) throws IOException;

    public abstract Account authenticate(String username, String authenticateId, String password,
            AuthContext.Protocol protocol, String origRemoteIp, String remoteIp, String userAgent) throws ServiceException;

    public abstract boolean isEncryptionEnabled();

    public abstract InputStream unwrap(InputStream is);

    public abstract OutputStream wrap(OutputStream os);

    public abstract SaslServer getSaslServer();

    public abstract void dispose();

    public void setConnection(Socket conn) {
        mConnection = conn;
    }
    
    public boolean isComplete() {
        return mComplete;
    }

    public boolean isAuthenticated() {
        return mAuthenticated;
    }

    public String getProtocol() {
        return mProtocol;
    }

    public String getMechanism() {
        return mMechanism;
    }

    public AuthenticatorUser getAuthenticatorUser() {
        return mAuthUser;
    }
    
    public void sendSuccess() throws IOException {
        mAuthUser.sendSuccessful();
    }
    
    public void sendFailed() throws IOException {
        mAuthUser.sendFailed();
        mComplete = true;
    }

    public void sendFailed(String msg) throws IOException {
        mAuthUser.sendFailed(msg);
    }
    
    public void sendBadRequest() throws IOException {
        mAuthUser.sendBadRequest("malformed authentication request");
        mComplete = true;
    }

    protected void sendContinuation(String s) throws IOException {
        mAuthUser.sendContinuation(s);
    }

    protected Log getLog() {
        return mAuthUser.getLog();
    }

    protected boolean authenticate(String authorizationId, String authenticationId, String password)
    throws IOException {
        mAuthenticated = mAuthUser.authenticate(authorizationId, authenticationId, password, this);
        mComplete = true;
        return mAuthenticated;
    }

    protected Account authorize(Account authAccount, String username, boolean asAdmin) throws ServiceException {
        if (username == null || username.length() == 0)
            return authAccount;

        Provisioning prov = Provisioning.getInstance();
        Account userAcct = prov.get(Provisioning.AccountBy.name, username);
        if (userAcct == null) {
            // if username not found, check username again using the domain associated with the authorization account
            int i = username.indexOf('@');
            if (i != -1) {
                String domain = authAccount.getDomainName();
                if (domain != null) {
                    username = username.substring(0, i) + '@' + domain;
                    userAcct = prov.get(Provisioning.AccountBy.name, username);
                }
            }
        }
        if (userAcct == null) {
            ZimbraLog.account.info("authorization failed for " + username + " (account not found)", username);
            return null;
        }

        // check whether the authenticated user is able to access the target
        if (!authAccount.getId().equals(userAcct.getId()) && !AccessManager.getInstance().canAccessAccount(authAccount, userAcct, asAdmin)) {
            ZimbraLog.account.warn("authorization failed for " + username + " (authenticated user " + authAccount.getName() + " has insufficient rights)");
            return null;
        }
        return userAcct;
    }
}