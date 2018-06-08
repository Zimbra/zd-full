/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.redolog.RedoLogInput;

public class RecoverItem extends CopyItem {

    public RecoverItem() {
        super();
        setFromDumpster(true);
    }

    public RecoverItem(int mailboxId, byte type, int folderId) {
        super(mailboxId, type, folderId);
        setFromDumpster(true);
    }

    @Override
    public int getOpCode() {
        return OP_RECOVER_ITEM;
    }

    @Override
    protected void deserializeData(RedoLogInput in) throws IOException {
        super.deserializeData(in);
        setFromDumpster(true);  // shouldn't be necessary, but let's be absolutely sure
    }
}
