/*
 * 
 */

/*
 * Created on 2004. 7. 21.
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class ModifySavedSearch extends RedoableOp {

    private int mSearchId;
    private String mQuery;
    private String mTypes;
    private String mSort;

    public ModifySavedSearch() {
        mSearchId = UNKNOWN_ID;
    }

    public ModifySavedSearch(int mailboxId, int searchId, String query, String types, String sort) {
        setMailboxId(mailboxId);
        mSearchId = searchId;
        mQuery = query != null ? query : "";
        mTypes = types != null ? types : "";
        mSort = sort != null ? sort : "";
    }

    @Override public int getOpCode() {
        return OP_MODIFY_SAVED_SEARCH;
    }

    @Override protected String getPrintableData() {
        StringBuffer sb = new StringBuffer("id=");
        sb.append(mSearchId).append(", query=").append(mQuery);
        sb.append(", types=").append(mTypes).append(", sort=").append(mSort);
        return sb.toString();
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeInt(mSearchId);
        out.writeUTF(mQuery);
        out.writeUTF(mTypes);
        out.writeUTF(mSort);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mSearchId = in.readInt();
        mQuery = in.readUTF();
        mTypes = in.readUTF();
        mSort = in.readUTF();
    }

    @Override public void redo() throws Exception {
        Mailbox mailbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        mailbox.modifySearchFolder(getOperationContext(), mSearchId, mQuery, mTypes, mSort);
    }
}
