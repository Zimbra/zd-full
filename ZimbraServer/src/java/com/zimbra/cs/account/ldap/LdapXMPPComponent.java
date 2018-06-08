/*
 * 
 */
package com.zimbra.cs.account.ldap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.XMPPComponent;
import com.zimbra.cs.account.Provisioning;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;


/**
 * 
 */
public class LdapXMPPComponent extends XMPPComponent implements LdapEntry {
    
    private String mDn;

    LdapXMPPComponent(String dn, Attributes attrs, Provisioning prov) throws NamingException, ServiceException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_cn),
              LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId),
              LdapUtil.getAttrs(attrs),
              prov
        );
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }

}
