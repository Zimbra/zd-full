/*
 * 
 */
/*
 * Created on Nov 12, 2005
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RevokeAccess extends RedoableOp {

    private int mFolderId;
    private String mGrantee;

    public RevokeAccess() {
        mFolderId = UNKNOWN_ID;
        mGrantee = "";
    }

    public RevokeAccess(int mailboxId, int folderId, String grantee) {
        setMailboxId(mailboxId);
        mFolderId = folderId;
        mGrantee = grantee == null ? "" : grantee;
    }

    @Override public int getOpCode() {
        return OP_REVOKE_ACCESS;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=").append(mFolderId);
        sb.append(", grantee=").append(mGrantee);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mFolderId);
        out.writeUTF(mGrantee);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mFolderId = in.readInt();
        mGrantee = in.readUTF();
    }

    @Override public void redo() throws ServiceException {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.revokeAccess(getOperationContext(), mFolderId, mGrantee);
    }
}
