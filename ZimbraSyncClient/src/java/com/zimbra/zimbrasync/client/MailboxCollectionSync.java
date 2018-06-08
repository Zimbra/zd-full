/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientDelete;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientItem;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncServerDelete;

public abstract class MailboxCollectionSync implements AirSyncServerDelete.Executor {
    
    public static class MailItemSyncState implements AirSyncClientItem {
        int id;
        int folderId;
        int changeId;
        
        public MailItemSyncState(int id, int folderId, int changeId) {
            this.id = id;
            this.folderId = folderId;
            this.changeId = changeId;
        }
        
        public String getClientId() {
            return Integer.toString(id);
        }
    }
    
    protected Mailbox mbox;
    protected Folder folder;
    protected String collectionId;
    protected ChangeTracker tracker;
    
    public MailboxCollectionSync(Folder folder, String collectionId, ChangeTracker tracker) {
        this.folder = folder;
        this.tracker = tracker;
        this.collectionId = collectionId;
        mbox = folder.getMailbox();
    }
    
    protected abstract byte getItemType();
    
    public abstract AirSyncClientAdd newClientAdd(int id) throws ServiceException, IOException;
    
    public abstract AirSyncClientChange newClientChange(int id) throws ServiceException, IOException;
    
    public final AirSyncClientDelete newClientDelete(int id) throws ServiceException {
        ExchangeItemMapping eim = tracker.mappingByClientId.get(id);
        assert eim != null : "id=" + id;
        return new AirSyncClientDelete(eim.getRemoteId(), tracker);
    }
    
    public void doServerDelete(String serverId) throws CommandCallbackException {
        try {
            ExchangeItemMapping mapping = tracker.getMapping(serverId);
            if (mapping != null) {
                if (mapping.getFolderId() == folder.getId()) {
                    mbox.delete(getContext(false), mapping.getItemId(), getItemType());
                    ZimbraLog.xsync.debug("deleted item (id=%d)", mapping.getItemId());
                    tracker.removeMapping(mapping);
                    mapping.delete();
                } else //due to client MoveItems or item moved on server but retained same ServerId
                    ZimbraLog.xsync.info("skip deleting item remote_id=%s in sync fid=%d but item is in fid=%d", serverId, folder.getId(), mapping.getFolderId());
            } else
                ZimbraLog.xsync.info("skip deleting item remote_id=%s not found in mapping", serverId);
        } catch (NoSuchItemException x) {
            ZimbraLog.xsync.info("skip deleting item remote_id=%s not found in mailbox", serverId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    protected OperationContext getContext(boolean markChanges) throws ServiceException {
        return ExchangeSyncFactory.getInstance().getContext(mbox, markChanges);
    }
}
