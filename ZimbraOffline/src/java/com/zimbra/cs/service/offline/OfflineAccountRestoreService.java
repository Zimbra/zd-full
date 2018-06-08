/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.offline.backup.AccountBackupProducer;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineAccountRestoreService extends DocumentHandler {
    
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        String accountId = request.getAttribute(AccountConstants.E_ID, null);
        String timestamp = request.getAttribute(AdminConstants.A_TIME, null);
        String resolve = request.getAttribute(OfflineConstants.A_RESOLVE, null);
        Element response = zsc.createElement(OfflineConstants.ACCOUNT_RESTORE_RESPONSE);
        response.addAttribute(AccountConstants.A_ID, accountId);
        response.addAttribute(AccountConstants.A_STATUS, AccountBackupProducer.getInstance().restoreAccount(accountId, Long.parseLong(timestamp), resolve));
        return response;
    }
}
