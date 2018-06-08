/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.HashMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;

public class MockMailboxManager extends MailboxManager {

    public MockMailboxManager() {
        super(true);
        mailboxes = new HashMap<String,Mailbox>();
    }

    @Override
    public Mailbox getMailboxByAccountId(String accountId)
        throws ServiceException {

        Mailbox mbox = mailboxes.get(accountId);
        if (mbox != null)
            return mbox;
        Account account = Provisioning.getInstance().getAccount(accountId);
        mbox = new MockMailbox(account);
        mailboxes.put(accountId, mbox);
        return mbox;
    }
    
    private HashMap<String,Mailbox> mailboxes;
}
