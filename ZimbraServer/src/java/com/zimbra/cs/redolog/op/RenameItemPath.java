/*
 * 
 */

/*
 * Created on 2004. 12. 13.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RenameItemPath extends RedoableOp {

    int mId;
    byte mType;
    String mPath;
    int mParentIds[];

    public RenameItemPath() {
        mId = UNKNOWN_ID;
        mType = MailItem.TYPE_UNKNOWN;
    }

    public RenameItemPath(int mailboxId, int id, byte type, String path) {
        setMailboxId(mailboxId);
        mId = id;
        mPath = path != null ? path : "";
    }

    public int[] getParentIds() {
        return mParentIds;
    }

    public void setParentIds(int parentIds[]) {
        mParentIds = parentIds;
    }

    @Override public int getOpCode() {
        return OP_RENAME_ITEM_PATH;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=");
        sb.append(mId).append(", type=").append(mType).append(", path=").append(mPath);
        if (mParentIds != null) {
            sb.append(", destParentIds=[");
            for (int i = 0; i < mParentIds.length; i++) {
                sb.append(mParentIds[i]);
                if (i < mParentIds.length - 1)
                    sb.append(", ");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeUTF(mPath);
        if (mParentIds != null) {
            out.writeInt(mParentIds.length);
            for (int i = 0; i < mParentIds.length; i++)
                out.writeInt(mParentIds[i]);
        } else {
            out.writeInt(0);
        }
        out.writeByte(mType);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mPath = in.readUTF();
        int numParentIds = in.readInt();
        if (numParentIds > 0) {
            mParentIds = new int[numParentIds];
            for (int i = 0; i < numParentIds; i++)
                mParentIds[i] = in.readInt();
        }
        mType = in.readByte();
    }

    @Override public void redo() throws Exception {
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mailbox.rename(getOperationContext(), mId, mType, mPath);
    }
}
