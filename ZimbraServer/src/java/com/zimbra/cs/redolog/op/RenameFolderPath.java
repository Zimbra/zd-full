/*
 * 
 */

/*
 * Created on 2004. 12. 13.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class RenameFolderPath extends RenameItemPath {

    public RenameFolderPath() {
        mId = UNKNOWN_ID;
        mType = MailItem.TYPE_FOLDER;
    }

    public RenameFolderPath(int mailboxId, int id, String path) {
        super(mailboxId, id, MailItem.TYPE_FOLDER, path);
    }

    @Override public int getOpCode() {
        return OP_RENAME_FOLDER_PATH;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mId);
        out.writeUTF(mPath);
        if (mParentIds != null) {
            out.writeInt(mParentIds.length);
            for (int i = 0; i < mParentIds.length; i++)
                out.writeInt(mParentIds[i]);
        } else {
            out.writeInt(0);
        }
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mId = in.readInt();
        mPath = in.readUTF();
        int numParentIds = in.readInt();
        if (numParentIds > 0) {
        	mParentIds = new int[numParentIds];
            for (int i = 0; i < numParentIds; i++)
            	mParentIds[i] = in.readInt();
        }
    }
}
