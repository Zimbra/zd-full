/*
 * 
 */

package com.zimbra.cs.account.ldap;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * @author schemers
 */
 class LdapIdentity extends Identity implements LdapEntry {

    private String mDn;

    LdapIdentity(Account acct, String dn, Attributes attrs, Provisioning prov) throws NamingException {
        super(  acct,
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraPrefIdentityName),
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraPrefIdentityId),
                LdapUtil.getAttrs(attrs), prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }

}
