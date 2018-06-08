/*
 * 
 */

package com.zimbra.cs.account.ldap;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.CalendarResource;
import com.zimbra.cs.account.Provisioning;

/**
 * @author jhahm
 */
class LdapCalendarResource extends CalendarResource implements LdapEntry {

    private String mDn;

    LdapCalendarResource(String dn, String email, Attributes attrs, Map<String, Object> defaults, Provisioning prov) throws NamingException {
        super(email,
              LdapUtil.getAttrString(attrs, Provisioning.A_zimbraId), 
              LdapUtil.getAttrs(attrs), defaults, prov);
        mDn = dn;
    }

    public String getDN() { return mDn; }
}
