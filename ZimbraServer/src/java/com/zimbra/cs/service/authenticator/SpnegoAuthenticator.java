/*
 * 
 */

package com.zimbra.cs.service.authenticator;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.SpnegoUserRealm;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.security.SpnegoUserRealm.SpnegoUser;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.AccountServiceException.AuthFailedServiceException;
import com.zimbra.cs.account.Provisioning.AccountBy;

public class SpnegoAuthenticator extends SSOAuthenticator {

    private SpnegoUserRealm spnegoUserRealm;
    
    public SpnegoAuthenticator(HttpServletRequest req, HttpServletResponse resp, SpnegoUserRealm spnegoUserRealm) {
        super(req, resp);
        this.spnegoUserRealm = spnegoUserRealm;
    }
    
    @Override
    public String getAuthType() {
        return "Spnego";
    }
    
    @Override
    public ZimbraPrincipal authenticate() throws ServiceException {
        Request request = (req instanceof Request) ? (Request)req : null;
        
        if (request == null) {
            throw ServiceException.FAILURE("not supported", null);
        }
        
        Principal principal = getPrincipal(request);
        Account acct = getAccountByPrincipal(principal);
        ZimbraPrincipal zimbraPrincipal = new ZimbraPrincipal(principal.getName(), acct);
        request.setUserPrincipal(zimbraPrincipal);
        
        return zimbraPrincipal;
    }
    
    private Principal getPrincipal(Request request) throws ServiceException {
        Principal principal = null;
        
        try {
            principal = authenticate(spnegoUserRealm, request, resp);
            
            // comment out the above and uncomment the line below for quick testing 
            // non-spenogo related issues, like redirect.
            // principal = MockSpnegoUser.getMockPrincipal();  
        } catch (IOException e) {
            throw AuthFailedServiceException.AUTH_FAILED("spnego authenticate failed", e); 
        }
        
        if (principal == null) {
            throw AuthFailedServiceException.AUTH_FAILED("spnego authenticate failed", (Throwable)null); 
        }
        
        return principal;
    }
    
    
    private Account getAccountByPrincipal(Principal principal) throws ServiceException {
        
        String krb5Name = principal.getName();
        
        Provisioning prov = Provisioning.getInstance();
        Account acct = prov.get(AccountBy.krb5Principal, krb5Name);
        return acct;
    }
    
    /* =========================================================
     * 
     * Based on org.mortbay.jetty.security.SpnegoAuthenticator
     * 
     * =========================================================
     */
    private Principal authenticate(UserRealm realm, Request request, HttpServletResponse response) 
    throws ServiceException, IOException {
        Principal user = null;
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        /*
         * if the header is null then we need to challenge...this is after the error page check
         */
        if (header == null) {
            sendChallenge(realm,request,response);
            throw SSOAuthenticatorServiceException.SENT_CHALLENGE();
            
        } else if (header != null && header.startsWith(HttpHeaders.NEGOTIATE)) {           
            /*
             * we have gotten a negotiate header to try and authenticate
             */
            
            // skip over "Negotiate "
            String username = header.substring(10);
            
            user = realm.authenticate(username, null, request);
            
            if (user != null) {
                ZimbraLog.account.debug("SpengoAuthenticator: obtained principal: " + user.getName());

                request.setAuthType(getAuthType());
                // request.setUserPrincipal(user);
                response.addHeader(HttpHeaders.WWW_AUTHENTICATE, HttpHeaders.NEGOTIATE + " " + ((SpnegoUser)user).getToken());
                
                return user;
            }
            else {
                /*
                 * no user was returned from the authentication which means something failed
                 * so process error logic
                 */
                ZimbraLog.account.debug("SpengoAuthenticator: no user found, authentication failed");
                throw AuthFailedServiceException.AUTH_FAILED("SpengoAuthenticator: no user found, authentication failed", (Throwable)null);
            }
        } else {
            /*
             * the header was not null, but we didn't get a negotiate so process error logic
             */
            throw AuthFailedServiceException.AUTH_FAILED(
                    "SpengoAuthenticator: authentication failed, unknown header (browser is likely misconfigured for SPNEGO)", (Throwable)null);
        }        
    }
    
    public void sendChallenge(UserRealm realm, Request request, HttpServletResponse response) throws IOException {
        ZimbraLog.account.debug("SpengoAuthenticator: sending challenge");
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HttpHeaders.NEGOTIATE);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    

    private static class MockSpnegoUser implements Principal {
        String name;
        String token;
        
        private static MockSpnegoUser getMockPrincipal() throws IOException {
            return new MockSpnegoUser("spnego@SPNEGO.LOCAL", "blah");
        }
        
        MockSpnegoUser(String name, String token) {
            this.name = name;
            this.token = token;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        public String getToken() {
            return token;
        }
        
    }

}
