/*
 * 
 */
/*
 * Created on Sep 19, 2005
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;
import java.util.Arrays;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class ColorItem extends RedoableOp {

    private int[] mIds;
    private byte mType;
    private long mColor;

    public ColorItem() { }

    public ColorItem(int mailboxId, int[] ids, byte type, MailItem.Color color) {
        setMailboxId(mailboxId);
        mIds = ids;
        mType = type;
        mColor = color.getValue();
    }

    @Override public int getOpCode() {
        return OP_COLOR_ITEM;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=");
        sb.append(Arrays.toString(mIds)).append(", color=").append(mColor);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(-1);
        out.writeByte(mType);
        // mColor from byte to long in Version 1.27
        out.writeLong(mColor);
        out.writeInt(mIds == null ? 0 : mIds.length);
        if (mIds != null) {
            for (int i = 0; i < mIds.length; i++)
                out.writeInt(mIds[i]);
        }
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        int id = in.readInt();
        if (id > 0)
            mIds = new int[] { id };
        mType = in.readByte();
        if (getVersion().atLeast(1, 27))
            mColor = in.readLong();
        else
            mColor = in.readByte();
        if (id <= 0) {
            mIds = new int[in.readInt()];
            for (int i = 0; i < mIds.length; i++)
                mIds[i] = in.readInt();
        }
    }

    @Override public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(mboxId);
        mailbox.setColor(getOperationContext(), mIds, mType, MailItem.Color.fromMetadata(mColor));
    }
}
