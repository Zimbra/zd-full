/*
 * 
 */
package com.zimbra.cs.offline.backup;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.offline.OfflineLog;

/**
 * Attribute change callback. Triggered by AttributeManager when interval is changed.
 *
 */
public class BackupIntervalCallback extends AttributeCallback {

    @SuppressWarnings("unchecked")
    @Override
    public void postModify(Map context, String attrName, Entry entry,
            boolean isCreate) {
        try {
            BackupTimer.updateInterval();
        } catch (ServiceException e) {
            OfflineLog.offline.error("Exception while updating backup interval",e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void preModify(Map context, String attrName, Object attrValue,
            Map attrsToModify, Entry entry, boolean isCreate)
            throws ServiceException {
        //do nada
    }

}
