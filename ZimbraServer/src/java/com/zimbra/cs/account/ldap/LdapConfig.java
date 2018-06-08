/*
 * 
 */
package com.zimbra.cs.account.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;

class LdapConfig extends Config implements LdapEntry {
    
    private String mDn;
    
    LdapConfig(String dn, Attributes attrs, Provisioning provisioning) throws NamingException {
        super(LdapUtil.getAttrs(attrs), provisioning);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
}
