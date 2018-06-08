/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class LockItem extends RedoableOp {

    protected int mId;
    protected byte mType;
    protected String mAccountId;

    public LockItem() { }

    public LockItem(int mailboxId, int id, byte type, String accountId) {
        setMailboxId(mailboxId);
        mId = id;
        mType = type;
        mAccountId = accountId;
    }

    @Override public int getOpCode() {
        return OP_LOCK_ITEM;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=").append(mId);
        sb.append(", type=").append(mType);
        sb.append(", accountId=").append(mAccountId);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeByte(mType);
        out.writeUTF(mAccountId);
        out.writeInt(mId);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mType = in.readByte();
        mAccountId = in.readUTF();
        mId = in.readInt();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.lock(getOperationContext(), mId, mType, mAccountId);
    }
}
