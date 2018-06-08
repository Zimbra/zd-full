/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class DismissCalendarItemAlarm extends RedoableOp {

    private int mId;
    private long mDismissedAt;

    public DismissCalendarItemAlarm() {
        mId = UNKNOWN_ID;
    }

    public DismissCalendarItemAlarm(int mailboxId, int id, long dismissedAt) {
        setMailboxId(mailboxId);
        mId = id;
        mDismissedAt = dismissedAt;
    }

    @Override public int getOpCode() {
        return OP_DISMISS_CALENDAR_ITEM_ALARM;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(mId).append(", dismissedAt=").append(mDismissedAt);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeLong(mDismissedAt);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mDismissedAt = in.readLong();
    }

    @Override public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(mboxId);
        mailbox.dismissCalendarItemAlarm(getOperationContext(), mId, mDismissedAt);
    }
}
