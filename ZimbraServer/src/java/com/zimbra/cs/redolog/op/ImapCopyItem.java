/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class ImapCopyItem extends RedoableOp {

    private Map<Integer, Integer> mDestIds = new HashMap<Integer, Integer>();
    private byte mType;
    private int mDestFolderId;

    public ImapCopyItem() {
        mType = MailItem.TYPE_UNKNOWN;
        mDestFolderId = 0;
    }

    public ImapCopyItem(int mailboxId, byte type, int folderId) {
        setMailboxId(mailboxId);
        mType = type;
        mDestFolderId = folderId;
    }

    /**
     * Sets the ID of the copied item.
     * @param destId
     */
    public void setDestId(int srcId, int destId) {
        mDestIds.put(srcId, destId);
    }

    public int getDestId(int srcId) {
        Integer destId = mDestIds.get(srcId);
        return destId == null ? -1 : destId;
    }

    @Override public int getOpCode() {
        return OP_IMAP_COPY_ITEM;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("type=").append(mType);
        sb.append(", destFolder=").append(mDestFolderId);
        sb.append(", [srcId, destId, srcImap]=");
        for (Map.Entry<Integer, Integer> entry : mDestIds.entrySet())
            sb.append('[').append(entry.getKey()).append(',').append(entry.getValue()).append(']');
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeByte(mType);
        out.writeInt(mDestFolderId);
        out.writeShort((short) -1);
        out.writeInt(mDestIds.size());
        for (Map.Entry<Integer, Integer> entry : mDestIds.entrySet()) {
            Integer srcId = entry.getKey();
            out.writeInt(srcId);
            out.writeInt(entry.getValue());
            out.writeInt(-1);                    // now unused; don't break the old format...
        }
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mType = in.readByte();
        mDestFolderId = in.readInt();
        in.readShort();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            Integer srcId = in.readInt();
            mDestIds.put(srcId, in.readInt());
            in.readInt();                        // now unused; don't break the old format...
        }
    }

    @Override public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(mboxId);

        int i = 0, itemIds[] = new int[mDestIds.size()];
        for (int id : mDestIds.keySet())
            itemIds[i++] = id;

        try {
            mbox.imapCopy(getOperationContext(), itemIds, mType, mDestFolderId);
        } catch (MailServiceException e) {
            if (e.getCode() == MailServiceException.ALREADY_EXISTS) {
                mLog.info("Item is already in mailbox " + mboxId);
                return;
            } else
                throw e;
        }
    }
}
