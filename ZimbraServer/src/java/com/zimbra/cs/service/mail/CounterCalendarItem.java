/*
 * 
 */

package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.soap.ZimbraSoapContext;

public class CounterCalendarItem extends CalendarRequest {

    private class InviteParser extends ParseMimeMessage.InviteParser { 
        public ParseMimeMessage.InviteParserResult parseInviteElement(
                ZimbraSoapContext lc, OperationContext octxt, Account account, Element inviteElem)
        throws ServiceException {
            return CalendarUtils.parseInviteForCounter(account, getItemType(), inviteElem);
        }
    };

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Account acct = getRequestedAccount(zsc);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);

        Element msgElem = request.getElement(MailConstants.E_MSG);
        InviteParser parser = new InviteParser();
        CalSendData dat = handleMsgElement(zsc, octxt, msgElem, acct, mbox, parser);

        mbox.getMailSender().sendMimeMessage(octxt, mbox, dat.mMm);
        Element response = getResponseElement(zsc);
        return response;
    }
}
