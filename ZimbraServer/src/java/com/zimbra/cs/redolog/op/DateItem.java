/*
 * 
 */
/*
 * Created on Nov 12, 2005
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class DateItem extends RedoableOp {

    private int mId;
    private byte mType;
    private long mDate;

    public DateItem() { }

    public DateItem(int mailboxId, int itemId, byte type, long date) {
        setMailboxId(mailboxId);
        mId = itemId;
        mType = type;
        mDate = date;
    }

    @Override public int getOpCode() {
        return OP_DATE_ITEM;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=").append(mId);
        sb.append(", date=").append(mDate);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeByte(mType);
        out.writeLong(mDate);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mType = in.readByte();
        mDate = in.readLong();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.setDate(getOperationContext(), mId, mType, mDate);
    }
}
