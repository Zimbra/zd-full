/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.CalendarSync.CalendarSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.CalendarSync.CalendarSyncClientChange;
import com.zimbra.zimbrasync.client.cmd.CalendarSync.CalendarSyncServerCommandExecutor;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange;
import com.zimbra.zimbrasync.data.CalendarAppData;
import com.zimbra.zimbrasync.data.MailboxCalendarAppData;
import com.zimbra.zimbrasync.data.ProtocolVersion;

public class MailboxCalendarSync extends MailboxCollectionSync implements CalendarSyncServerCommandExecutor {

    public MailboxCalendarSync(Folder folder, String collectionId, ChangeTracker tracker) {
        super(folder, collectionId, tracker);
    }
    
    @Override
    protected byte getItemType() {
        return MailItem.TYPE_APPOINTMENT;
    }

    @Override
    public AirSyncClientAdd newClientAdd(int id) throws ServiceException, IOException {
        synchronized (mbox) {
            CalendarItem calItem = mbox.getCalendarItemById(tracker.getContext(false), id);
            MailItemSyncState miss = new MailItemSyncState(id, folder.getId(), calItem.getModifiedSequence());
            MailboxCalendarAppData mcad = new MailboxCalendarAppData(getContext(false), calItem, new ProtocolVersion("2.5"));
            CalendarSyncClientAdd add = new CalendarSyncClientAdd(miss, tracker, mcad.getCalendarAppData());
            return add;
        }
    }

    @Override
    public AirSyncClientChange newClientChange(int id) throws ServiceException, IOException {
        synchronized (mbox) {
            CalendarItem calItem = mbox.getCalendarItemById(tracker.getContext(false), id);
            MailItemSyncState miss = new MailItemSyncState(id, folder.getId(), calItem.getModifiedSequence());
            ExchangeItemMapping eim = tracker.mappingByClientId.get(id);
            assert eim != null : "id=" + id;
            MailboxCalendarAppData mcad = new MailboxCalendarAppData(getContext(false), calItem, new ProtocolVersion("2.5"));
            CalendarSyncClientChange change = new CalendarSyncClientChange(eim.getRemoteId(), miss, tracker, mcad.getCalendarAppData());
            return change;
        }
    }
    
    public void doServerAdd(String serverId, CalendarAppData appData) throws CommandCallbackException {
        try {
            ExchangeItemMapping mapping = tracker.getMapping(serverId);
            if (mapping != null) { //possibly an interrupted sync or we did a MoveItems from client
                ZimbraLog.xsync.debug("skip adding existing calendar item (id=%d;ServerId=%s)", mapping.getItemId(), mapping.getRemoteId());
                synchronized (mbox) {
                    if (mapping.getFolderId() != folder.getId()) { //item moved on server but serverId didn't change
                        ZimbraLog.xsync.debug("adding calendar item remote_id=%s to fid=%d but item exists in fid=%d. moving.", serverId, folder.getId(), mapping.getFolderId());
                        mbox.move(getContext(false), mapping.getItemId(), getItemType(), folder.getId()); //TODO: type
                        mapping.setFolderId(folder.getId());
                        mapping.update();
                    }
                    if (!tracker.getClientChanges(folder.getId()).contains(mapping.getItemId())) { //if item updated locally, don't apply remote change. we'll push up.
                        doServerChange(serverId, appData);
                    }
                }
            } else {
                synchronized (mbox) {
                    MailboxCalendarAppData mcad = new MailboxCalendarAppData(appData);
                    CalendarItem calItem = mcad.setCalendarItem(mbox, getContext(false), folder.getId(), null, false); //TODO: isTask
                    ZimbraLog.xsync.debug("added calender item '%s' (id=%d)", calItem.getSubject(), calItem.getId());
                    mapping = new ExchangeItemMapping(tracker.getDataSource(), folder.getId(), calItem.getId(), serverId, collectionId);
                    mapping.add();
                }                
            }
        } catch (Exception x) {
            throw new CommandCallbackException(x);
        }
        
    }
    
    public void doServerChange(String serverId, CalendarAppData appData) throws CommandCallbackException {
        try {
            synchronized (mbox) {
                ExchangeItemMapping mapping = tracker.getMapping(serverId);
                assert mapping != null : "remote_id=" + serverId;
                CalendarItem oldCalItem = mbox.getCalendarItemById(getContext(false), mapping.getItemId());
                MailboxCalendarAppData mcad = new MailboxCalendarAppData(appData);
                CalendarItem calItem = mcad.setCalendarItem(mbox, getContext(false), folder.getId(), oldCalItem, false); //TODO: isTask
                assert calItem.getId() == oldCalItem.getId();
                ZimbraLog.xsync.debug("updated calendar item (id=%d)", mapping.getItemId());
            }
        } catch (NoSuchItemException x) {
            ZimbraLog.xsync.info("modifying item remote_id=%s not found", serverId);
        } catch (Exception x) {
            throw new CommandCallbackException(x);
        }
    }
}
