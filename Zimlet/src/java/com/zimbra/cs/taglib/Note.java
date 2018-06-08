/*
 * 
 */
package com.zimbra.cs.taglib;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.OperationContext;

public class Note extends ZimbraTag {
    private static final long serialVersionUID = -3525900802675257570L;

    private String mId;

    @Override
    public void setId(String val) {
        mId = val;
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        if (mId == null) {
            throw ZimbraTagException.MISSING_ATTR("id");
        }
        int mid = Integer.parseInt(mId);
//        String id = acct.getId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(id);
        com.zimbra.cs.mailbox.Note note = mbox.getNoteById(octxt, mid);

        return note.getText();
    }
}
