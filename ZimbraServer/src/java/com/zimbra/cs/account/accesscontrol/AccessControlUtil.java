/*
 * 
 */
package com.zimbra.cs.account.accesscontrol;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.GuestAccount;
import com.zimbra.cs.account.Provisioning;

public class AccessControlUtil {
    
    public static boolean isGlobalAdmin(Account acct) {
        return isGlobalAdmin(acct, true);
    }
    
    public static boolean isGlobalAdmin(Account acct, boolean asAdmin) {
        return (asAdmin && acct != null && acct.isIsAdminAccount());
    }
    
    static boolean isDelegatedAdmin(Account acct, boolean asAdmin) {
        return (asAdmin && acct != null && acct.isIsDelegatedAdminAccount());
    }
    
    static public Account authTokenToAccount(AuthToken authToken, Right rightNeeded) {
        Account granteeAcct = null;
        try {
            
            if (authToken == null) {
                if (rightNeeded.isUserRight()) {
                    granteeAcct = GuestAccount.ANONYMOUS_ACCT;
                }
            } else if (authToken.isZimbraUser()) {
                granteeAcct = authToken.getAccount();
            } else {
                if (rightNeeded.isUserRight()) {
                    granteeAcct = new GuestAccount(authToken);
                }
            }
        } catch (ServiceException e) {
            ZimbraLog.acl.warn("unable to get account from auth token, id=: " + 
                    authToken.getAccountId(), e);
        }
        
        return granteeAcct;
    }
    
    static public Account emailAddrToAccount(String emailAddr, Right rightNeeded) {
        Account granteeAcct = null;
        try {
            if (emailAddr != null) {
                granteeAcct = Provisioning.getInstance().get(Provisioning.AccountBy.name, emailAddr);
            }
            
            if (granteeAcct == null) {
                // not an internal user
                if (rightNeeded.isUserRight()) {
                    if (emailAddr != null) {
                        granteeAcct = new GuestAccount(emailAddr, null);
                    } else {
                        granteeAcct = GuestAccount.ANONYMOUS_ACCT;
                    }
                }
            }
            
        } catch (ServiceException e) {
            ZimbraLog.acl.warn("unable to get account from email address: " + emailAddr, e);
        }
        
        return granteeAcct;
    }
}
