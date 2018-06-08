/*
 * 
 */

package com.zimbra.cs.account.callback;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.filter.RuleManager;
 
public class MailSieveScript extends AttributeCallback {

    /**
     * check to make sure zimbraMailHost points to a valid server zimbraServiceHostname
     */
    @SuppressWarnings("unchecked")
    public void preModify(Map context, String attrName, Object value,
            Map attrsToModify, Entry entry, boolean isCreate) throws ServiceException {

        singleValueMod(attrName, value);
        
        if (!(entry instanceof Account)) 
            return;
        
        Account acct = (Account)entry;
        
        if (!Provisioning.onLocalServer(acct))
            return;
        
        // clear it from the in memory parsed filter rule cache
        RuleManager.clearCachedRules(acct);
    }

    public void postModify(Map context, String attrName, Entry entry, boolean isCreate) {
    }
}
