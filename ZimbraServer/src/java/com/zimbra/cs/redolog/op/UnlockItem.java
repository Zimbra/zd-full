/*
 * 
 */
package com.zimbra.cs.redolog.op;

import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox;

public class UnlockItem extends LockItem {

    public UnlockItem() { 
        super();
    }

    public UnlockItem(int mailboxId, int id, byte type, String accountId) {
        super(mailboxId, id, type, accountId);
    }

    @Override public int getOpCode() {
        return OP_UNLOCK_ITEM;
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.unlock(getOperationContext(), mId, mType, mAccountId);
    }
}
