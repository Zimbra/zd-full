/*
 * 
 */

package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.soap.ZimbraSoapContext;

public class AnnounceOrganizerChange extends CalendarRequest {

    private static final String[] TARGET_PATH = new String[] { MailConstants.A_ID };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return false; }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account acct = getRequestedAccount(zsc);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        ItemId iid = new ItemId(request.getAttribute(MailConstants.A_ID), zsc);

        MailSendQueue sendQueue = new MailSendQueue();
        Element response = getResponseElement(zsc);
        try {
            synchronized(mbox) {
                CalendarItem calItem = mbox.getCalendarItemById(octxt, iid.getId());
                sendOrganizerChangeMessage(zsc, octxt, calItem, acct, mbox, sendQueue);
            }
        } finally {
            sendQueue.send();
        }
        return response;
    }
}
