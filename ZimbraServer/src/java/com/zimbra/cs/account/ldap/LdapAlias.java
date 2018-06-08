/*
 * 
 */

package com.zimbra.cs.account.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.Alias;
import com.zimbra.cs.account.Provisioning;

class LdapAlias extends Alias implements LdapEntry {
    private String mDn;

    LdapAlias(String dn, String email, Attributes attrs, Provisioning prov) throws NamingException
    {
        super(email,
              LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId), 
              LdapUtil.getAttrs(attrs), prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
}
