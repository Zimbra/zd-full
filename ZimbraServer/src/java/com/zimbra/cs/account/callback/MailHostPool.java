/*
 * 
 */

package com.zimbra.cs.account.callback;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.ServerBy;

public class MailHostPool extends AttributeCallback {

    /**
     * check to make sure zimbraMailHostPool points to a valid server id
     */
    public void preModify(Map context, String attrName, Object value,
            Map attrsToModify, Entry entry, boolean isCreate) throws ServiceException {
        
        MultiValueMod mod = multiValueMod(attrsToModify, Provisioning.A_zimbraMailHostPool);
        
        if (mod.adding() || mod.replacing()) {
            Provisioning prov = Provisioning.getInstance();
            List<String> pool = mod.values();
            for (String host : pool) {
                if (host == null || host.equals("")) continue;
                if (prov.get(ServerBy.id, host) == null)
                    throw ServiceException.INVALID_REQUEST("specified "+Provisioning.A_zimbraMailHostPool+" does not correspond to a valid server: "+host, null);
            }
        }
    }

    public void postModify(Map context, String attrName, Entry entry, boolean isCreate) {

    }
}
