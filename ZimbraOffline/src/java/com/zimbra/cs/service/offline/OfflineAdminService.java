/*
 * 
 */

/*
 * Created on Jul 30, 2010
 */
package com.zimbra.cs.service.offline;

import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.DocumentDispatcher;

public class OfflineAdminService extends AdminService {

    @Override
    public void registerHandlers(DocumentDispatcher dispatcher) {
        super.registerHandlers(dispatcher);
        dispatcher.registerHandler(AdminConstants.DELETE_MAILBOX_REQUEST, new OfflineDeleteMailbox());
        dispatcher.registerHandler(AdminConstants.DELETE_ACCOUNT_REQUEST, new OfflineDeleteAccount());
        dispatcher.registerHandler(OfflineConstants.RESET_GAL_ACCOUNT_REQUEST, new OfflineResetGal());
    }

}
