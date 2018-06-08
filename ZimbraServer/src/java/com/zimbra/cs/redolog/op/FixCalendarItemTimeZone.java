/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class FixCalendarItemTimeZone extends RedoableOp {

    private int mId;
    private long mAfter;
    private String mCountry;  // ISO-3166 two-letter country code, or null for world

    public FixCalendarItemTimeZone() {}

    public FixCalendarItemTimeZone(int mailboxId, int itemId, long after, String country) {
        setMailboxId(mailboxId);
        mId = itemId;
        mAfter = after;
        mCountry = country;
    }

    @Override public int getOpCode() {
        return OP_FIX_CALENDAR_ITEM_TIME_ZONE;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(mId);
        sb.append(", after=").append(mAfter);
        sb.append(", country=").append(mCountry);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeLong(mAfter);
        out.writeUTF(mCountry);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mAfter = in.readLong();
        mCountry = in.readUTF();
    }

    @Override public void redo() throws Exception {
        // do nothing; this op has been deprecated
    }
}
