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

import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;

/**
 * @author schemers
 */
public class LdapDomain extends Domain implements LdapEntry {

    private String mDn;

    LdapDomain(String dn, Attributes attrs, Map<String, Object> defaults, Provisioning prov) throws NamingException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_zimbraDomainName), 
                LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId), 
                LdapUtil.getAttrs(attrs), defaults, prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
}
