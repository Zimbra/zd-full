/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;
import java.io.InputStream;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Message;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.EmailSync.EmailAppData;
import com.zimbra.zimbrasync.client.cmd.EmailSync.EmailSyncClientChange;
import com.zimbra.zimbrasync.client.cmd.EmailSync.EmailSyncServerCommandExcecutor;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange;

public final class MailboxEmailSync extends MailboxCollectionSync implements EmailSyncServerCommandExcecutor {

    public MailboxEmailSync(Folder folder, String collectionId, ChangeTracker tracker) {
        super(folder, collectionId, tracker);
    }
    
    @Override
    protected byte getItemType() {
        return MailItem.TYPE_MESSAGE;
    }

    @Override
    public AirSyncClientAdd newClientAdd(int id) throws ServiceException {
        assert false;
        throw new RuntimeException("email client add not supported");
    }

    @Override
    public AirSyncClientChange newClientChange(int id) throws ServiceException {
        Message msg = mbox.getMessageById(tracker.getContext(false), id);
        MailItemSyncState miss = new MailItemSyncState(id, folder.getId(), msg.getModifiedSequence());
        ExchangeItemMapping eim = tracker.mappingByClientId.get(id);
        assert eim != null : "id=" + id;
        EmailSyncClientChange change = new EmailSyncClientChange(eim.getRemoteId(), miss, tracker);
        change.setRead(!msg.isUnread());
        return change;
    }
    
    static final int PRIORITY_LOW = 0;
    static final int PRIORITY_NORMAL = 1;
    static final int PRIORITY_HIGH = 2;
    
    public void doServerAdd(String serverId, EmailAppData appData, InputStream in) throws CommandCallbackException, IOException {
        try {
            boolean isSkipped = false;
            ExchangeItemMapping mapping = tracker.getMapping(serverId);
            if (mapping != null) { //possibly an interrupted sync or we did a MoveItems from client
                synchronized (mbox) {
                    try {
                        isSkipped = mbox.getMessageById(getContext(false), mapping.getItemId()) != null;
                    } catch (NoSuchItemException x) {
                        ZimbraLog.xsync.debug("redownloading deleted message (id=%d;ServerId=%s)", mapping.getItemId(), mapping.getRemoteId());
                    }
                    if (isSkipped) {
                        if (mapping.getFolderId() != folder.getId()) { //item moved on server but serverId didn't change
                            ZimbraLog.xsync.debug("adding message remote_id=%s to fid=%d but item exists in fid=%d. moving.", serverId, folder.getId(), mapping.getFolderId());
                            mbox.move(getContext(false), mapping.getItemId(), MailItem.TYPE_MESSAGE, folder.getId());
                            mapping.setFolderId(folder.getId());
                            mapping.update();
                        }
                        if (!tracker.getClientChanges(folder.getId()).contains(mapping.getItemId())) { //if item updated locally, don't apply remote change. we'll push up.
                            Message msg = mbox.getMessageById(getContext(false), mapping.getItemId());
                            if (msg.isUnread() == appData.isRead()) { //equal means different
                                mbox.alterTag(getContext(false), msg.getId(), MailItem.TYPE_MESSAGE, Flag.ID_FLAG_UNREAD, !appData.isRead());
                            }
                        }
                    }
                }
            }
            
            if (isSkipped) {
                assert mapping != null;
                ZimbraLog.xsync.debug("skip adding existing message (id=%d;ServerId=%s)", mapping.getItemId(), mapping.getRemoteId());
                ByteUtil.skip(in, Long.MAX_VALUE);
            } else {
                int flags = appData.isRead() ? 0 : Flag.BITMASK_UNREAD;
                switch (appData.getImportance()) {
                case PRIORITY_HIGH:
                    flags |= Flag.BITMASK_HIGH_PRIORITY;
                    break;
                case PRIORITY_LOW:
                    flags |= Flag.BITMASK_LOW_PRIORITY;
                    break;
                }
                Message msg = mbox.addMessage(getContext(false), in, appData.getMimeSize(), appData.getDataReceived(), folder.getId(), true, flags, "", Mailbox.ID_AUTO_INCREMENT, ":API:", null, null);
                ZimbraLog.xsync.debug("added message '%s' (id=%d)", msg.getSubject(), msg.getId());
                mapping = new ExchangeItemMapping(tracker.getDataSource(), folder.getId(), msg.getId(), serverId, collectionId);
                mapping.add();
            }
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    public void doServerChange(String serverId, boolean isRead) throws CommandCallbackException {
        try {
            synchronized (mbox) {
                ExchangeItemMapping mapping = tracker.getMapping(serverId);
                assert mapping != null : "remote_id=" + serverId;
                mbox.alterTag(getContext(false), mapping.getItemId(), MailItem.TYPE_MESSAGE, Flag.ID_FLAG_UNREAD, !isRead);
                ZimbraLog.xsync.debug("updated message (id=%d)", mapping.getItemId());
            }
        } catch (NoSuchItemException x) {
            ZimbraLog.xsync.info("modifying item remote_id=%s not found", serverId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
}
