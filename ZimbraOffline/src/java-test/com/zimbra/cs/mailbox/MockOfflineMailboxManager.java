/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.HashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox.MailboxData;

public class MockOfflineMailboxManager extends MailboxManager {

    public enum Type { ZCS, DESKTOP};

    private Type mboxType = Type.DESKTOP;

    public MockOfflineMailboxManager(Type type) throws ServiceException {
        super(true);
        mailboxes = new HashMap<String,DesktopMailbox>();
        mboxType = type;
    }

    @Override
    public Mailbox getMailboxByAccountId(String accountId)
        throws ServiceException {

        DesktopMailbox mbox = mailboxes.get(accountId);
        if (mbox != null)
            return mbox;
        Account account = Provisioning.getInstance().getAccount(accountId);
        switch (mboxType) {
            case DESKTOP :  mbox = new MockDesktopMailbox();
                            break;
            case ZCS     :  MailboxData data = new MailboxData();
                            data.accountId = account.getId();
                            mbox = new MockZcsMailbox(account, data);
                            break;
        }
        mailboxes.put(accountId, mbox);
        return mbox;
    }

    private HashMap<String,DesktopMailbox> mailboxes;
}
