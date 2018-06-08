/*
 * 
 */

/*
 * Created on Sep 23, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.account.ldap;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;

/**
 * @author schemers
 */
class LdapServer extends Server implements LdapEntry {

    private String mDn;

    LdapServer(String dn, Attributes attrs, Map<String,Object> defaults, Provisioning prov) throws NamingException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_cn), 
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId), 
                LdapUtil.getAttrs(attrs), defaults, prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
}
