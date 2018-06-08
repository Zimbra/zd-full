/*
 * 
 */

package com.zimbra.cs.account.ldap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * @author schemers
 */
class LdapDataSource extends DataSource implements LdapEntry {

	static String getObjectClass(Type type) {
		switch (type) {
		case pop3: return "zimbraPop3DataSource";
		case imap: return "zimbraImapDataSource";
		case rss:  return "zimbraRssDataSource";
		case gal:  return "zimbraGalDataSource";
		default: return null;
		}
	}

	static Type getObjectType(Attributes attrs) throws ServiceException {
		try {
			String dsType = LdapUtil.getAttrString(attrs, Provisioning.A_zimbraDataSourceType);
			if (dsType != null)
				return Type.fromString(dsType);
		} catch (NamingException e) {
			ZimbraLog.datasource.error("cannot get DataSource type", e);
		}
		Attribute attr = attrs.get("objectclass");
		if (attr.contains("zimbraPop3DataSource")) 
			return Type.pop3;
		else if (attr.contains("zimbraImapDataSource"))
			return Type.imap;
		else if (attr.contains("zimbraRssDataSource"))
		    return Type.rss;
		else if (attr.contains("zimbraGalDataSource"))
            return Type.gal;
		else
			throw ServiceException.FAILURE("unable to determine data source type from object class", null);
	}

	private String mDn;

	LdapDataSource(Account acct, String dn, Attributes attrs, Provisioning prov) throws NamingException, ServiceException {
		super(acct, 
				getObjectType(attrs),
				LdapUtil.getAttrString(attrs, Provisioning.A_zimbraDataSourceName),
				LdapUtil.getAttrString(attrs, Provisioning.A_zimbraDataSourceId),                
				LdapUtil.getAttrs(attrs), prov);
		mDn = dn;
	}
	public String getDN() {
		return mDn;
	}
}
