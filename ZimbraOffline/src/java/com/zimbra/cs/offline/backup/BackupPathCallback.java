/*
 * 
 */
package com.zimbra.cs.offline.backup;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.Entry;

/**
 * Backup Attribute callback. Validates that the specified directory is readable and writable 
 *
 */
public class BackupPathCallback extends AttributeCallback {

    @SuppressWarnings("unchecked")
    @Override
    public void postModify(Map context, String attrName, Entry entry,
            boolean isCreate) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void preModify(Map context, String attrName, Object attrValue,
            Map attrsToModify, Entry entry, boolean isCreate)
            throws ServiceException {
        if (!(attrValue instanceof String)) {
            throw ServiceException.INVALID_REQUEST(attrName+" must be a String", null);
        }
        BackupPropertyManager.getInstance().validateBackupPath((String) attrValue);
    }
}
