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
 * Callback class for handling auto-archiving frequency attribute (zimbraPrefAutoArchiveFrequency) modification.
 */
public class AutoArchiveFrequencyCallback extends AttributeCallback {
    @SuppressWarnings("unchecked")
    @Override
    public void postModify(Map context, String attrName, Entry entry,
            boolean isCreate) {
        String freq = entry.getAttr(attrName);
        OfflineLog.offline.info("Auto archive frequency is set to %s", freq);
        try {
            if (AutoArchive.isAutoArchivingEnabled()) {
                AutoArchiveTimer.rescheduleAutoArchiving();
            }
        } catch (ServiceException e) {
            OfflineLog.offline.error("Exception occured while rescheduling", e);
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
