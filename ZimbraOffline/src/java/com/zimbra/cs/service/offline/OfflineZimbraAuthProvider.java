/*
 * 
 */

/*
 * Created on Aug 20, 2010
 */
package com.zimbra.cs.service.offline;

import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.service.AuthProviderException;
import com.zimbra.cs.service.ZimbraAuthProvider;

public class OfflineZimbraAuthProvider extends ZimbraAuthProvider {
    
    public static final String PROVIDER_NAME = "offline";

    public OfflineZimbraAuthProvider() {
        super(PROVIDER_NAME);
    }
    
    protected AuthToken genAuthToken(String encodedAuthToken) throws AuthProviderException, AuthTokenException {
        if (StringUtil.isNullOrEmpty(encodedAuthToken))
            throw AuthProviderException.NO_AUTH_DATA();
        AuthToken at = ZimbraAuthToken.getAuthToken(encodedAuthToken);
        if (at instanceof ZimbraAuthToken) {
           try {
               return (AuthToken)((ZimbraAuthToken)at).clone();
           } catch (CloneNotSupportedException e) {
               ZimbraLog.system.error("Unable to clone zimbra auth token",e);
               return at;
           }
        } else {
            return at;
        }
    }
}
