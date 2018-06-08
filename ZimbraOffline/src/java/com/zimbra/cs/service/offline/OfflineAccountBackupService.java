/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.backup.AccountBackupProducer;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineAccountBackupService extends DocumentHandler {
    
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        String id = request.getAttribute(AdminConstants.E_ID, null);
        Map<String,String> status = null;
        if (id != null) {
            status = AccountBackupProducer.getInstance().backupAccounts(new String[]{id});
        } else {
            status = AccountBackupProducer.getInstance().backupAllAccounts();
        }
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(OfflineConstants.ACCOUNT_BACKUP_RESPONSE);
        for (String acctId : status.keySet()) {
            String backupStatus = status.get(acctId);
            Element acctElem = response.addElement(AdminConstants.E_ACCOUNT);
            acctElem.addAttribute(AdminConstants.E_ID, acctId);
            acctElem.addAttribute(AdminConstants.E_STATUS, backupStatus);
        }
        return response;
    }
}
