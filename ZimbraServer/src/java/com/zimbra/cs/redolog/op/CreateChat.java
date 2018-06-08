/*
 * 
 */
package com.zimbra.cs.redolog.op;

import java.io.IOException;

import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.redolog.RedoLogInput;
import com.zimbra.cs.redolog.RedoLogOutput;

public class CreateChat extends CreateMessage {
    
    public CreateChat() {
    }

    public CreateChat(int mailboxId, String digest, int msgSize, int folderId, int flags, String tags) {
        super(mailboxId, ":API:", false, digest, msgSize, folderId, true, flags, tags);
    }
    
    @Override public int getOpCode() {
        return OP_CREATE_CHAT;
    }
    
    @Override protected void serializeData(RedoLogOutput out) throws IOException {
        super.serializeData(out);
    }

    @Override protected void deserializeData(RedoLogInput in) throws IOException {
        super.deserializeData(in);
    }
    
    @Override public void redo() throws Exception {
        int mboxId = getMailboxId();
        Mailbox mbox = MailboxManager.getInstance().getMailboxById(mboxId);

        ParsedMessage  pm = new ParsedMessage(getMessageBody(), getTimestamp(), mbox.attachmentsIndexingEnabled());
        try {
            mbox.createChat(getOperationContext(), pm, getFolderId(), getFlags(), getTags());
        } catch (MailServiceException e) {
            if (e.getCode() == MailServiceException.ALREADY_EXISTS) {
                mLog.info("Chat " + getMessageId() + " is already in mailbox " + mboxId);
                return;
            } else {
                throw e;
            }
        }
    }
    
}
