/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.ContactsSync.ContactsSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.ContactsSync.ContactsSyncClientChange;
import com.zimbra.zimbrasync.client.cmd.ContactsSync.ContactsSyncServerCommandExecutor;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange;
import com.zimbra.zimbrasync.data.ContactAppData;
import com.zimbra.zimbrasync.data.MailboxContactAppData;
import com.zimbra.zimbrasync.data.ProtocolVersion;

public class MailboxContactsSync extends MailboxCollectionSync implements ContactsSyncServerCommandExecutor {

    public MailboxContactsSync(Folder folder, String collectionId, ChangeTracker tracker) {
        super(folder, collectionId, tracker);
    }
    
    @Override
    protected byte getItemType() {
        return MailItem.TYPE_CONTACT;
    }

    @Override
    public AirSyncClientAdd newClientAdd(int id) throws ServiceException, IOException {
        synchronized (mbox) {
            Contact contact = mbox.getContactById(tracker.getContext(false), id);
            MailItemSyncState miss = new MailItemSyncState(id, folder.getId(), contact.getModifiedSequence());
            MailboxContactAppData mcad = new MailboxContactAppData(getContext(false), contact, new ProtocolVersion("2.5"));
            ContactsSyncClientAdd add = new ContactsSyncClientAdd(miss, tracker, mcad.getContactAppData());
            return add;
        }
    }

    @Override
    public AirSyncClientChange newClientChange(int id) throws ServiceException, IOException {
        synchronized (mbox) {
            Contact contact = mbox.getContactById(tracker.getContext(false), id);
            MailItemSyncState miss = new MailItemSyncState(id, folder.getId(), contact.getModifiedSequence());
            ExchangeItemMapping eim = tracker.mappingByClientId.get(id);
            assert eim != null : "id=" + id;
            MailboxContactAppData mcad = new MailboxContactAppData(getContext(false), contact, new ProtocolVersion("2.5"));
            ContactsSyncClientChange change = new ContactsSyncClientChange(eim.getRemoteId(), miss, tracker, mcad.getContactAppData());
            return change;
        }
    }
    
    public void doServerAdd(String serverId, ContactAppData appData) throws CommandCallbackException {
        try {
            ExchangeItemMapping mapping = tracker.getMapping(serverId);
            if (mapping != null) { //possibly an interrupted sync or we did a MoveItems from client
                ZimbraLog.xsync.debug("skip adding existing contact (id=%d;ServerId=%s)", mapping.getItemId(), mapping.getRemoteId());
                synchronized (mbox) {
                    if (mapping.getFolderId() != folder.getId()) { //item moved on server but serverId didn't change
                        ZimbraLog.xsync.debug("adding contact remote_id=%s to fid=%d but item exists in fid=%d. moving.", serverId, folder.getId(), mapping.getFolderId());
                        mbox.move(getContext(false), mapping.getItemId(), MailItem.TYPE_CONTACT, folder.getId());
                        mapping.setFolderId(folder.getId());
                        mapping.update();
                    }
                    if (!tracker.getClientChanges(folder.getId()).contains(mapping.getItemId())) { //if item updated locally, don't apply remote change. we'll push up.
                        doServerChange(serverId, appData);
                    }
                }
            } else {
                synchronized (mbox) {
                    MailboxContactAppData mcad = new MailboxContactAppData(appData);
                    Contact contact = mcad.createContact(mbox, getContext(false), folder.getId());
                    ZimbraLog.xsync.debug("added contact '%s' (id=%d)", contact.getFileAsString(), contact.getId());
                    mapping = new ExchangeItemMapping(tracker.getDataSource(), folder.getId(), contact.getId(), serverId, collectionId);
                    mapping.add();
                }                
            }
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    public void doServerChange(String serverId, ContactAppData appData) throws CommandCallbackException {
        try {
            synchronized (mbox) {
                ExchangeItemMapping mapping = tracker.getMapping(serverId);
                assert mapping != null : "remote_id=" + serverId;
                MailboxContactAppData mcad = new MailboxContactAppData(appData);
                mcad.modifyContact(mbox, getContext(false), mapping.getItemId());
                ZimbraLog.xsync.debug("updated contact (id=%d)", mapping.getItemId());
            }
        } catch (NoSuchItemException x) {
            ZimbraLog.xsync.info("modifying item remote_id=%s not found", serverId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
}
