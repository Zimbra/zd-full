/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.backup.AccountBackupInfo;
import com.zimbra.cs.offline.backup.AccountBackupProducer;
import com.zimbra.cs.offline.backup.BackupInfo;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineBackupEnumService extends DocumentHandler {
    
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        Set<AccountBackupInfo> info = AccountBackupProducer.getInstance().getStoredBackups();
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(OfflineConstants.ACCOUNT_BACKUP_ENUM_RESPONSE);
        for (AccountBackupInfo acctInfo : info) {
            Element acctElem = response.addElement(AdminConstants.E_ACCOUNT);
            acctElem.addAttribute(AdminConstants.E_ID, acctInfo.getAccountId());
            for (BackupInfo backup : acctInfo.getBackups()) {
                Element backupElem = acctElem.addElement(OfflineConstants.E_BACKUP);
                backupElem.addAttribute(AdminConstants.A_TIME, backup.getTimestamp());
                backupElem.addAttribute(AdminConstants.A_FILE_SIZE, backup.getFile().length());
            }
        }
        return response;
    }
}
