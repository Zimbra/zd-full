/*
 * 
 */
package com.zimbra.cs.account.offline;

import java.util.Map;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Signature;

class OfflineSignature extends Signature {
    OfflineSignature(Account acct, String name, Map<String,Object> attrs, Provisioning prov) {
        super(acct, name, (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }
    
    OfflineSignature(Account acct, Map<String,Object> attrs, Provisioning prov) {
        super(acct, (String)attrs.get(Provisioning.A_zimbraSignatureName), (String)attrs.get(Provisioning.A_zimbraSignatureId), attrs, prov);
    }

    void setName(String name) {
        mName = name;
    }
}
