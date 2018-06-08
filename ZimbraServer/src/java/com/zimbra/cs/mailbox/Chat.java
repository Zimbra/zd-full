/*
 * 
 */
package com.zimbra.cs.mailbox;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.store.StagedBlob;

public class Chat extends Message {

    /**
     * this one will call back into decodeMetadata() to do our initialization
     * 
     * @param mbox
     * @param ud
     * @throws ServiceException
     */
    Chat(Mailbox mbox, UnderlyingData ud) throws ServiceException {
        super(mbox, ud);
        if (mData.type != TYPE_CHAT)
            throw new IllegalArgumentException();
        if (mData.parentId < 0)
            mData.parentId = -mId;
    }

    static class ChatCreateFactory extends MessageCreateFactory {
        @Override Message create(Mailbox mbox, UnderlyingData data) throws ServiceException {
            return new Chat(mbox, data);
        }
        @Override byte getType()  { return TYPE_CHAT; }
    }

    static Chat create(int id, Folder folder, ParsedMessage pm, StagedBlob staged, boolean unread, int flags, long tags)  
    throws ServiceException, IOException {
        return (Chat) Message.createInternal(id, folder, null, pm, staged, unread, flags, tags, null, true, null, null, new ChatCreateFactory());
    }

    @Override boolean isMutable() { return true; }
}
