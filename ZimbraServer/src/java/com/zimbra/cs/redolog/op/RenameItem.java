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

public class RenameItem extends RedoableOp {

    int mId;
    byte mType;
    int mFolderId;
    String mName;

    public RenameItem() {
        mId = mFolderId = UNKNOWN_ID;
        mType = MailItem.TYPE_UNKNOWN;
    }

    public RenameItem(int mailboxId, int id, byte type, String name, int folderId) {
        setMailboxId(mailboxId);
        mId = id;
        mType = type;
        mFolderId = folderId;
        mName = name != null ? name : "";
    }

    @Override public int getOpCode() {
        return OP_RENAME_ITEM;
    }

    @Override protected String getPrintableData() {
        return "id=" + mId + ", type=" + mType + ", name=" + mName + ",parent=" + mFolderId;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeInt(mFolderId);
        out.writeUTF(mName);
        out.writeByte(mType);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mFolderId = in.readInt();
        mName = in.readUTF();
        mType = in.readByte();
    }

    @Override public void redo() throws Exception {
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mailbox.rename(getOperationContext(), mId, mType, mName, mFolderId);
    }
}
