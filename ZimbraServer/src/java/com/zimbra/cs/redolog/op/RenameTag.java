/*
 * 
 */

/*
 * Created on 2004. 7. 21.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RenameTag extends RenameItem {

    public RenameTag() {
        super();
        mType = MailItem.TYPE_TAG;
        mFolderId = Mailbox.ID_FOLDER_TAGS;
    }

    public RenameTag(int mailboxId, int tagId, String name) {
        super(mailboxId, tagId, MailItem.TYPE_TAG, name, Mailbox.ID_FOLDER_TAGS);
    }

    @Override public int getOpCode() {
        return OP_RENAME_TAG;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeUTF(mName);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mName = in.readUTF();
    }
}
