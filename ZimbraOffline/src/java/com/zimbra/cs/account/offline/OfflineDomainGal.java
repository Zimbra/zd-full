/*
 * 
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.offline.common.OfflineConstants;

/**
 * directory:
 *          entry_id | entry_type | entry_name             | zimbraId | 
 *              dd   |   Gal      | xxx.com     |  aaaa-bbbb-cccc-dddd
 *
 * directory_attrs:
 *          entry_id | name                     | value
 *              dd   | cn                       | xxx.com
 *              dd   | objectClass              | domainGalEntry
 *              dd   | zimbraId                 | aaaa-bbbb-cccc-dddd
 *              dd   | offlineGalRetryEnabled   | TRUE
 *              dd   | offlineGalAccountId      | 1234-2323-232-3323 (Gal Account zimbraId)
 *              dd   | offlineUsingGalAccountId | 1111-2222-3333-4444 (Account which uses this as their GAL)
 *              dd   | offlineUsingGalAccountId | 1111-2222-3333-5555 (Account which uses this as their GAL)
 *
 */
public class OfflineDomainGal extends NamedEntry {

    protected OfflineDomainGal(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, null, prov);
    }

    public static Map<String, OfflineDomainGal> instantiateAll(OfflineProvisioning prov) {
        Map<String, OfflineDomainGal> map = new HashMap<String, OfflineDomainGal>();
        try {
            List<String> ids = DbOfflineDirectory.listAllDirectoryEntries(EntryType.GAL);
            for (String id : ids) {
                Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.GAL,
                        Provisioning.A_zimbraId, id);
                if (attrs == null)
                    continue;
                String domainName = (String) attrs.get(Provisioning.A_cn);
                if (domainName != null)
                    map.put(domainName.toLowerCase(), new OfflineDomainGal(domainName, id, attrs, prov));
            }
            return map;
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating offlineDomainGal", e);
        }
    }

    public String[] getAttachedToGalAccountIds() {
        return getMultiAttr(OfflineProvisioning.A_offlineUsingGalAccountId);
    }

    public String getDomain() {
        return getAttr(Provisioning.A_cn);
    }

    public String getId() {
        return getAttr(Provisioning.A_zimbraId);
    }

    public Account getGalAccount() throws ServiceException {
        return OfflineProvisioning.getOfflineInstance().getAccountById(getGalAccountId());
    }

    public String getGalAccountId() {
        return getAttr(OfflineConstants.A_offlineGalAccountId);
    }

    public boolean isRetryEnabled() {
        return getBooleanAttr(OfflineProvisioning.A_offlineGalRetryEnabled, true);
    }
}
