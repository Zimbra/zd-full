/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.calendar.ICalTimeZone;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class ICalReply extends RedoableOp {

    private Invite mInvite;
    
    public ICalReply()  {}

    public ICalReply(int mailboxId, Invite inv) {
        setMailboxId(mailboxId);
        mInvite = inv;
    }

    @Override public int getOpCode() {
        return OP_ICAL_REPLY;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder();
        ICalTimeZone localTz = mInvite.getTimeZoneMap().getLocalTimeZone();
        sb.append("localTZ=").append(localTz.encodeAsMetadata().toString());
        sb.append(", inv=").append(Invite.encodeMetadata(mInvite).toString());
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        ICalTimeZone localTz = mInvite.getTimeZoneMap().getLocalTimeZone();
        out.writeUTF(localTz.encodeAsMetadata().toString());
        out.writeUTF(Invite.encodeMetadata(mInvite).toString());
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        try {
            ICalTimeZone localTz = ICalTimeZone.decodeFromMetadata(new Metadata(in.readUTF()));
            mInvite = Invite.decodeMetadata(getMailboxId(), new Metadata(in.readUTF()), null, localTz);
        } catch (ServiceException ex) {
            ex.printStackTrace();
            throw new IOException("Cannot read serialized entry for ICalReply " + ex.toString());
        }
    }
    
    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mbox.processICalReply(getOperationContext(), mInvite);
    }
}
