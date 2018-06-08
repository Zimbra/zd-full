/*
 * 
 */
package com.zimbra.cs.redolog.op;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class PurgeImapDeleted extends RedoableOp {

    public PurgeImapDeleted() {
    }

    public PurgeImapDeleted(int mailboxId) {
        setMailboxId(mailboxId);
    }

    @Override
    public int getOpCode() {
        return OP_PURGE_IMAP_DELETED;
    }

    @Override
    protected String getPrintableData() {
        // no members to print
        return null;
    }

    @Override
    protected void serializeData(RedoLogOutput out) {
        // no members to serialize
    }

    @Override
    protected void deserializeData(RedoLogInput in) {
        // no members to deserialize
    }

    @Override
    public void redo() throws ServiceException {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.purgeImapDeleted(getOperationContext());
    }

    @Override
    public boolean isDeleteOp() {
        return true;
    }
}
