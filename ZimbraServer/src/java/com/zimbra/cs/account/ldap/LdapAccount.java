/*
 * 
 */

package com.zimbra.cs.account.ldap;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;

/**
 * @author schemers
 */
 class LdapAccount extends Account implements LdapEntry {

    private String mDn;
    
    LdapAccount(String dn, String email, Attributes attrs, Map<String, Object> defaults, Provisioning prov) throws NamingException {
        super(email,
              LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId),
              LdapUtil.getAttrs(attrs), defaults, prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }

}
