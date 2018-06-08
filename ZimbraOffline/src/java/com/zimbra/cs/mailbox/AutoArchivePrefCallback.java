/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.offline.OfflineLog;

/**
 * Callback class for handling auto-archiving preference change (zimbraPrefAutoArchiveEnabled) modification.
 */
public class AutoArchivePrefCallback extends AttributeCallback {
    @SuppressWarnings("unchecked")
    @Override
    public void postModify(Map context, String attrName, Entry entry,
            boolean isCreate) {
        boolean isAutoArchiveEnabled = entry.getBooleanAttr(attrName, false);

        OfflineLog.offline.info("AutoArchivePrefChanged: Auto archiving is %s", isAutoArchiveEnabled ? "enabled" : "disabled");

        //Auto-archiving is disabled
        if(!isAutoArchiveEnabled) {
            OfflineLog.offline.debug("Cancelling auto-archiving");
            AutoArchiveTimer.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void preModify(Map context, String attrName, Object attrValue,
            Map attrsToModify, Entry entry, boolean isCreate)
                    throws ServiceException {
        //do nothing
    }
}
