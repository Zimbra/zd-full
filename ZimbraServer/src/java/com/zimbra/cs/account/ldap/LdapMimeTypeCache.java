/*
 * 
 */
package com.zimbra.cs.account.ldap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.cache.IMimeTypeCache;
import com.zimbra.cs.mime.MimeTypeInfo;

class LdapMimeTypeCache implements IMimeTypeCache {
	
	private List<MimeTypeInfo> mAllMimeTypes;
	private Map<String, List<MimeTypeInfo>> mMapByMimeType;
	private long mRefreshTTL;
	private long mLifetime;
	
	LdapMimeTypeCache() {
		mRefreshTTL = LC.ldap_cache_mime_maxage.intValue() * Constants.MILLIS_PER_MINUTE;
	}
	
	@Override
	public synchronized void flushCache(Provisioning prov) throws ServiceException {
		refresh((LdapProvisioning)prov);
	}
	
	@Override
	public synchronized List<MimeTypeInfo> getAllMimeTypes(Provisioning prov) 
	throws ServiceException {
		refreshIfNecessary((LdapProvisioning)prov);
		return mAllMimeTypes;
	}
	
	@Override
	public synchronized List<MimeTypeInfo> getMimeTypes(Provisioning prov, String mimeType)
	throws ServiceException {
	    
	    LdapProvisioning ldapProv = (LdapProvisioning) prov;
	    
		refreshIfNecessary(ldapProv);
		List<MimeTypeInfo> mimeTypes = mMapByMimeType.get(mimeType);
		if (mimeTypes == null) {
			mimeTypes = Collections.unmodifiableList(ldapProv.getMimeTypesByQuery(mimeType));
			mMapByMimeType.put(mimeType, mimeTypes);
		}
		return mimeTypes;
	}
	
	private void refreshIfNecessary(LdapProvisioning prov) throws ServiceException {
		if (isStale())
			refresh(prov);
	}
	
	private boolean isStale() {
		if (mAllMimeTypes == null)
			return true;
		
        return mRefreshTTL != 0 && mLifetime < System.currentTimeMillis();
    }
	
	private void refresh(LdapProvisioning prov) throws ServiceException {
		mAllMimeTypes = Collections.unmodifiableList(prov.getAllMimeTypesByQuery());
		mMapByMimeType = new HashMap<String, List<MimeTypeInfo>>();
		mLifetime = System.currentTimeMillis() + mRefreshTTL;
	}
}
