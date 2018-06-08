/*
 * 
 */

/*
 * Created on Sep 23, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.account.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.Provisioning;

/**
 * @author schemers
 */
class LdapCos extends Cos implements LdapEntry {

    private String mDn;

    LdapCos(String dn, Attributes attrs, Provisioning prov) throws NamingException, ServiceException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_cn), LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId), LdapUtil.getAttrs(attrs), prov);
        mDn = dn;
    }

    public String getDN() {
        return mDn; 
    }
}
