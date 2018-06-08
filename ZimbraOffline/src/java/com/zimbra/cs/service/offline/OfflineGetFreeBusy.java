/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.service.mail.GetFreeBusy;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.account.Account;
import com.zimbra.common.soap.MailConstants;

public class OfflineGetFreeBusy extends GetFreeBusy {
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext ctxt = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(ctxt);
        if (mbox instanceof ZcsMailbox) {
            Account acct = ((ZcsMailbox)mbox).getAccount();
            String uid = request.getAttribute(MailConstants.A_UID);
            if (uid.equals(acct.getName())) // no need to do proxy for organizer
                return super.handle(request, context);          
        }
        
        return new OfflineServiceProxy("get free/busy", false, true, MailConstants.GET_FREE_BUSY_RESPONSE).handle(request, context);
    }
}
