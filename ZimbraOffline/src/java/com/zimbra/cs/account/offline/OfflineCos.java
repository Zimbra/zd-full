/*
 * 
 */
package com.zimbra.cs.account.offline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Cos;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineProvisioning.EntryType;
import com.zimbra.cs.db.DbOfflineDirectory;
import com.zimbra.cs.offline.OfflineLC;

class OfflineCos extends Cos {
    OfflineCos(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, prov);
    }

    static OfflineCos instantiate(Provisioning prov) {
        try {
            Map<String, Object> attrs = DbOfflineDirectory.readDirectoryEntry(EntryType.COS, OfflineProvisioning.A_offlineDn, "default");
            if (attrs == null) {
                attrs = new HashMap<String, Object>(3);
                attrs.put(Provisioning.A_cn, "default");
                attrs.put(Provisioning.A_objectClass, "zimbraCOS");
                attrs.put(Provisioning.A_zimbraId, UUID.randomUUID().toString());
                DbOfflineDirectory.createDirectoryEntry(EntryType.COS, "default", attrs, false);
            }
            
            //make sure auth token doesn't expire too soon
            attrs.put(Provisioning.A_zimbraAuthTokenLifetime, OfflineLC.auth_token_lifetime.value());
            attrs.put(Provisioning.A_zimbraAdminAuthTokenLifetime, OfflineLC.auth_token_lifetime.value());
            
            //allow proxy to any domains
            attrs.put(Provisioning.A_zimbraProxyAllowedDomains, "*");
            
            return new OfflineCos("default", (String) attrs.get(Provisioning.A_zimbraId), attrs, prov);
        } catch (ServiceException e) {
            // throw RuntimeException because we're being called at startup...
            throw new RuntimeException("failure instantiating default cos", e);
        }
    }
}
