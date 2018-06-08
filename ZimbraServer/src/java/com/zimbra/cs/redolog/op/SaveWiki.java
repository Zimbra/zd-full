/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class SaveWiki extends SaveDocument {

    private String mWikiword;

    public SaveWiki() {
    }

    public SaveWiki(int mailboxId, String digest, int msgSize, int folderId) {
        super(mailboxId, digest, msgSize, folderId);
    }

    @Override public int getOpCode() {
        return OP_SAVE_WIKI;
    }

    public String getWikiword() {
        return mWikiword;
    }

    public void setWikiword(String w) {
        mWikiword = w;
    }

    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        out.writeUTF(mWikiword);
        super.serializeData(out);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        mWikiword = in.readUTF();
        super.deserializeData(in);
    }

    @Override public void redo() throws Exception {
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(getMailboxId());
        try {
            mbox.createWiki(getOperationContext(), getFolderId(), mWikiword, getAuthor(), getDescription(), getAdditionalDataStream());
        } catch (MailServiceException e) {
            if (e.getCode() == MailServiceException.ALREADY_EXISTS) {
                mLog.info("Wiki " + getMessageId() + " is already in mailbox " + mbox.getId());
                return;
            } else {
                throw e;
            }
        }
    }
}
