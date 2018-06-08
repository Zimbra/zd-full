/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mailbox.MailItem.CustomMetadata;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class SetCustomData extends RedoableOp {

    private int mId;
    private byte mType;
    private CustomMetadata mExtendedData;

    public SetCustomData() { }

    public SetCustomData(int mailboxId, int id, byte type, CustomMetadata custom) {
        setMailboxId(mailboxId);
        mId = id;
        mType = type;
        mExtendedData = custom;
    }

    @Override public int getOpCode() {
        return OP_SET_CUSTOM_DATA;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=");
        sb.append(mId).append(", data=").append(mExtendedData);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeByte(mType);
        out.writeUTF(mExtendedData.getSectionKey());
        out.writeUTF(mExtendedData.getSerializedValue());
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mType = in.readByte();
        String extendedKey = in.readUTF();
        mExtendedData = new CustomMetadata(extendedKey, in.readUTF());
    }

    @Override public void redo() throws Exception {
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mailbox.setCustomData(getOperationContext(), mId, mType, mExtendedData);
    }
}
