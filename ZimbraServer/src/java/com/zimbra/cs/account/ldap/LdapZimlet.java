/*
 * 
 */
package com.zimbra.cs.account.ldap;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Zimlet;

class LdapZimlet extends Zimlet implements LdapEntry {

    private String mDn;
    
	public LdapZimlet(String dn, Attributes attrs, Provisioning prov) throws NamingException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_cn),
                LdapUtil.getAttrString(attrs, Provisioning.A_cn),                 
                LdapUtil.getAttrs(attrs), prov);
        mDn = dn;
	}
	
    public String getDN() {
        return mDn;
    }
}
