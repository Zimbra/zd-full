/*
 * 
 */
package com.zimbra.cs.taglib;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;

public class Conversation extends Message {
    private static final long serialVersionUID = -2306183433671648674L;

    String mIndex;

    public void setIndex(String val) {
        mIndex = val;
    }

    public String getIndex() {
        return mIndex;
    }

    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        if (mField == null) {
            throw ZimbraTagException.MISSING_ATTR("field");
        }
        if (mIndex == null) {
            throw ZimbraTagException.MISSING_ATTR("index");
        }
        int cid = Integer.parseInt(mId);
        int index = Integer.parseInt(mIndex);
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(acct.getId());
        List<com.zimbra.cs.mailbox.Message> msgs = mbox.getMessagesByConversation(octxt, cid);
        return getMessageContent(msgs.get(index));
    }
}
