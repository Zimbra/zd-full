/*
 * 
 */
/*
 * Created on Sep 23, 2005
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class CreateMountpoint extends RedoableOp {

    private int mId;
    private int mFolderId;
    private String mName;
    private String mOwnerId;
    private int mRemoteId;
    private byte mDefaultView;
    private int mFlags;
    private long mColor;

    public CreateMountpoint() {
        mId = UNKNOWN_ID;
    }

    public CreateMountpoint(int mailboxId, int folderId, String name, String ownerId, int remoteId,
                            byte view, int flags, MailItem.Color color) {
        setMailboxId(mailboxId);
        mId = UNKNOWN_ID;
        mFolderId = folderId;
        mName = name != null ? name : "";
        mOwnerId = ownerId;
        mRemoteId = remoteId;
        mDefaultView = view;
        mFlags = flags;
        mColor = color.getValue();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    @Override public int getOpCode() {
        return OP_CREATE_LINK;
    }

    @Override protected String getPrintableData() {
        StringBuilder sb = new StringBuilder("id=").append(mId);
        sb.append(", name=").append(mName).append(", folder=").append(mFolderId);
        sb.append(", owner=").append(mOwnerId).append(", remote=").append(mRemoteId);
        sb.append(", view=").append(mDefaultView).append(", flags=").append(mFlags).append(", color=").append(mColor);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeUTF(mName);
        out.writeUTF(mOwnerId);
        out.writeInt(mRemoteId);
        out.writeInt(mFolderId);
        out.writeByte(mDefaultView);
        out.writeInt(mFlags);
        // mColor from byte to long in Version 1.27
        out.writeLong(mColor);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mName = in.readUTF();
        mOwnerId = in.readUTF();
        mRemoteId = in.readInt();
        mFolderId = in.readInt();
        mDefaultView = in.readByte();
        mFlags = in.readInt();
        if (getVersion().atLeast(1, 27))
            mColor = in.readLong();
        else
            mColor = in.readByte();
    }

    @Override public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(mboxId);

        try {
            mailbox.createMountpoint(getOperationContext(), mFolderId, mName, mOwnerId, mRemoteId, mDefaultView, mFlags, MailItem.Color.fromMetadata(mColor));
        } catch (MailServiceException e) {
            if (e.getCode() == MailServiceException.ALREADY_EXISTS) {
                if (mLog.isInfoEnabled())
                    mLog.info("Mount " + mId + " already exists in mailbox " + mboxId);
            } else
                throw e;
        }
    }
}
