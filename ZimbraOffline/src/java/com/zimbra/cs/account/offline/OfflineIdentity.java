/*
 * 
 */
package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;

class OfflineIdentity extends Identity {
    OfflineIdentity(Account acct, String name, Map<String,Object> attrs, Provisioning prov) {
        super(acct, name, (String) attrs.get(Provisioning.A_zimbraPrefIdentityId), attrs, prov);
    }

    void setName(String name) {
        mName = name;
    }
}
