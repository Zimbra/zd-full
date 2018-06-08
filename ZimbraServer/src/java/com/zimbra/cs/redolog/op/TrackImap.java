/*
 * 
 */

/*
 * Created on Feb 11, 2006
 */
package com.zimbra.cs.redolog.op;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class TrackImap extends RedoableOp {

    public TrackImap() {
    }

    public TrackImap(int mailboxId) {
        setMailboxId(mailboxId);
    }

    @Override public int getOpCode() {
        return OP_TRACK_IMAP;
    }

    @Override protected String getPrintableData() {
        // no members to print
        return null;
    }

    @Override protected void serializeData(RedoLogOutput out) {
        // no members to serialize
    }

    @Override protected void deserializeData(RedoLogInput in) {
        // no members to deserialize
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.beginTrackingImap();
    }
}
