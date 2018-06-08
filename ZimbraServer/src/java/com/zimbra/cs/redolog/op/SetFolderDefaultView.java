/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class SetFolderDefaultView extends RedoableOp {

    private int mFolderId;
    private byte mDefaultView;

    public SetFolderDefaultView() {
        mFolderId = Mailbox.ID_AUTO_INCREMENT;
        mDefaultView = MailItem.TYPE_UNKNOWN;
    }

    public SetFolderDefaultView(int mailboxId, int folderId, byte view) {
        setMailboxId(mailboxId);
        mFolderId = folderId;
        mDefaultView = view;
    }

    @Override public int getOpCode() {
        return OP_SET_DEFAULT_VIEW;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=").append(mFolderId);
        sb.append(", view=").append(mDefaultView);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mFolderId);
        out.writeByte(mDefaultView);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mFolderId = in.readInt();
        mDefaultView = in.readByte();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.setFolderDefaultView(getOperationContext(), mFolderId, mDefaultView);
    }
}