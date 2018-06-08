/*
 * 
 */

package com.zimbra.cs.gal;

import java.util.Arrays;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;

public class ZimbraGalGroupHandler extends GalGroupHandler {

    private static String[] sEmptyMembers = new String[0];
    
    @Override
    public boolean isGroup(SearchResult sr) {
        Attributes ldapAttrs = sr.getAttributes();
        Attribute objectclass = ldapAttrs.get(Provisioning.A_objectClass);
        if (objectclass == null) {
            return false;
        } else {
            return objectclass.contains(LdapProvisioning.C_zimbraMailList);
        }
    }
    
    @Override
    public String[] getMembers(ZimbraLdapContext zlc, String searchBase, SearchResult sr) {
        try {
            ZimbraLog.gal.debug("Fetching members for group " + LdapUtil.getAttrString(sr.getAttributes(), LdapProvisioning.A_mail));
            Attributes ldapAttrs = sr.getAttributes();
            String[] members = LdapUtil.getMultiAttrString(ldapAttrs, Provisioning.A_zimbraMailForwardingAddress);
            Arrays.sort(members);
            return members;
        } catch (NamingException e) {
            ZimbraLog.gal.warn("unable to retrieve group members ", e);
            return sEmptyMembers;
        }
    }
}
