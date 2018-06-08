/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RenameMailbox extends RedoableOp {

    private String mNewName;
    private String mOldName;

    public RenameMailbox() {}

    public RenameMailbox(int mailboxId, String oldName, String newName) {
        setMailboxId(mailboxId);
        mNewName = newName;
    }

    @Override public int getOpCode() {
        return OP_RENAME_MAILBOX;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeUTF(mNewName);
        if (getVersion().atLeast(1,25))
            out.writeUTF(mOldName);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mNewName = in.readUTF();
        if (getVersion().atLeast(1,25)) 
            mOldName = in.readUTF();
    }

    @Override protected String getPrintableData() {
        return "newName=" + mNewName+" oldName="+mOldName;
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        if (mNewName != null)
            mbox.renameMailbox(mOldName, mNewName);
    }
}
