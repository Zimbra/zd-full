/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class FixCalendarItemEndTime extends RedoableOp {

    private int mId;

    public FixCalendarItemEndTime()  {}

    public FixCalendarItemEndTime(int mailboxId, int itemId) {
        setMailboxId(mailboxId);
        mId = itemId;
    }

    @Override public int getOpCode() {
        return OP_FIX_CALENDAR_ITEM_END_TIME;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(mId);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        OperationContext octxt = getOperationContext();
        CalendarItem calItem = mbox.getCalendarItemById(octxt, mId);
        if (calItem != null)
            mbox.fixCalendarItemEndTime(octxt, calItem);
    }
}
