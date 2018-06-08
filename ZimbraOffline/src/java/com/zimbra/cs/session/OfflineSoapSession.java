/*
 * 
 */
package com.zimbra.cs.session;

import java.util.Iterator;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;

public class OfflineSoapSession extends SoapSession {

    public OfflineSoapSession(String authenticatedId, boolean asAdmin) {
        super(authenticatedId, asAdmin);
    }

    @Override
    protected void addRemoteNotifications(RemoteNotifications rns) {
        removeUnqualifiedRemoteNotifications(rns);
        super.addRemoteNotifications(rns);
    }

    private void removeUnqualifiedRemoteNotifications(RemoteNotifications rns) {
        if (rns == null || rns.count == 0) {
            return;
        }
        removeUnqualifiedRemoteNotifications(rns.created);
        removeUnqualifiedRemoteNotifications(rns.modified);
    }

    private void removeUnqualifiedRemoteNotifications(List<Element> notifs) {
        if (notifs == null) {
            return;
        }
        //we don't want to add acct id, just want to see if formatting *requires* id to be added
        ItemIdFormatter ifmt = new ItemIdFormatter("any","any", false); 
        Iterator<Element> it = notifs.iterator();
        while (it.hasNext()) {
            Element elt = it.next();
            String itemIdStr = null;
            try {
                itemIdStr = elt.getAttribute(A_ID);
            } catch (ServiceException se) {
                continue;
            }
            ItemId item = null;
            try {
                item = new ItemId(itemIdStr,ifmt.getAuthenticatedId());
            } catch (ServiceException e) {
                continue;
            }
            if (item != null && !item.toString().equals(itemIdStr)) {
                it.remove();
            }
        }
    }

    @Override
    public RegisterNotificationResult registerNotificationConnection(final PushChannel sc) throws ServiceException {
        RegisterNotificationResult result = super.registerNotificationConnection(sc);
        if (result == RegisterNotificationResult.BLOCKING && OfflineSyncManager.getInstance().hasPendingStatusChanges()) {
            //if any pending sync state changes make it DATA_READY
            result = RegisterNotificationResult.DATA_READY; 
        }
        return result;
    }
}
