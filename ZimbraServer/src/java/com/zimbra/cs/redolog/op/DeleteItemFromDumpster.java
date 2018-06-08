/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;
import java.util.Arrays;

import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class DeleteItemFromDumpster extends RedoableOp {

    private int[] mIds;

    public DeleteItemFromDumpster() {
    }

    public DeleteItemFromDumpster(int mailboxId, int[] ids) {
        setMailboxId(mailboxId);
        mIds = ids;
    }

    @Override public int getOpCode() {
        return OP_DELETE_ITEM_FROM_DUMPSTER;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("ids=");
        sb.append(Arrays.toString(mIds));
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mIds.length);
        for (int id : mIds)
            out.writeInt(id);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mIds = new int[in.readInt()];
        for (int i = 0; i < mIds.length; i++)
            mIds[i] = in.readInt();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());

        try {
            mbox.deleteFromDumpster(getOperationContext(), mIds);
        } catch (MailServiceException.NoSuchItemException e) {
            if (mLog.isInfoEnabled())
                mLog.info("Some of the items being deleted were already deleted from dumpster " + getMailboxId());
        }
    }

    @Override public boolean isDeleteOp() {
        return true;
    }
}
