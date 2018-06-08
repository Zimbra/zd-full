/*
 * 
 */

/*
 * Created on Jul 30, 2010
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.admin.DeleteAccount;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineDeleteAccount extends DeleteAccount {

    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();

        String id = request.getAttribute(AdminConstants.E_ID);

        // Confirm that the account exists and that the mailbox is located
        // on the current host
        Account account = prov.get(AccountBy.id, id, zsc.getAuthToken());
        if (account == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(id);

        OfflineLog.offline.debug("delete account request received for acct %s",account.getName());
        checkAccountRight(zsc, account, Admin.R_deleteAccount);        
        if (account instanceof OfflineAccount)
        {
            if (((OfflineAccount)account).isDisabledDueToError()) {
                OfflineLog.offline.debug("deleting bad mailbox");
                OfflineMailboxManager omgr = (OfflineMailboxManager) MailboxManager.getInstance();
                omgr.purgeBadMailboxByAccountId(account.getId());
            } else {
                Mailbox mbox = Provisioning.onLocalServer(account) ? 
                        MailboxManager.getInstance().getMailboxByAccount(account, false) : null;
                if (mbox != null) {
                    OfflineLog.offline.debug("deleting mailbox");
                    mbox.deleteMailbox();
                }
            }
        }
        prov.deleteAccount(id);
        ZimbraLog.security.info(ZimbraLog.encodeAttrs(
            new String[] {"cmd", "DeleteAccount","name", account.getName(), "id", account.getId()}));

        Element response = zsc.createElement(AdminConstants.DELETE_ACCOUNT_RESPONSE);
        return response;
    }

}
