/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mailbox.OfflineMailboxManager;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.service.mail.ImportData;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineImportData extends ImportData {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        MailboxManager mmgr = MailboxManager.getInstance();
        if (!(mmgr instanceof OfflineMailboxManager))
            return super.handle(request, context);

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        if (!(mbox instanceof ZcsMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());
        ZcsMailbox ombx = (ZcsMailbox) mbox;

        // before doing anything, make sure all data sources are pushed to the server
        ombx.sync(true, false);
        // proxy this operation to the remote server
        Element response = ombx.sendRequest(request);
        // and get a head start on the sync of the newly-pulled-in messages
        ombx.sync(true, false);

        return response;
    }
}
