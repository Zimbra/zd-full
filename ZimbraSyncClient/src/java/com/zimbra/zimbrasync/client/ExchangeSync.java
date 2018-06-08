/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import java.util.TreeMap;
import java.util.Map.Entry;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.RemoteServiceException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.MailItemImport;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.EmailFetch;
import com.zimbra.zimbrasync.client.cmd.HttpStatusException;
import com.zimbra.zimbrasync.client.cmd.Provision;
import com.zimbra.zimbrasync.client.cmd.Request;
import com.zimbra.zimbrasync.client.cmd.ResponseStatusException;
import com.zimbra.zimbrasync.client.cmd.Sync;
import com.zimbra.zimbrasync.client.cmd.SyncSettings;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;

public class ExchangeSync extends MailItemImport {
    private ExchangeSyncFactory syncFactory;
    private SyncSettings syncSettings;

    public ExchangeSync(DataSource ds) throws ServiceException {
        super(ds);
        syncFactory = ExchangeSyncFactory.getInstance();
        syncSettings = syncFactory.getSyncSettings(ds);
    }

    public synchronized void test() throws ServiceException {
        try {
            new Request(syncSettings, syncFactory.getPolicyKey(mbox)).doOptions();
        } catch (HttpStatusException.UnauthorizedException x) {
            throw RemoteServiceException.AUTH_FAILURE("xsync", x);
        } catch (HttpStatusException.ForbiddenException x) {
            throw RemoteServiceException.AUTH_DENIED("xsync", x);
        } catch (HttpStatusException.BadRequestException x) {
            throw RemoteServiceException.PROTOCOL_EXCEPTION("xsync", x);
        } catch (HttpStatusException x) {
            throw ServiceException.FAILURE("xsync", x); //TODO
        } catch (IOException x) {
            throw RemoteServiceException.CONNECT_FAILURE("xsync", x);
        }
    }

    public void importData(List<Integer> folderIds, boolean fullSync) throws ServiceException {
        sync(false);
    }

    private void sync(boolean isRetry) throws ServiceException {
        boolean isDebugTraceOn = getDataSource().isDebugTraceEnabled();
        try {
            ChangeTracker tracker = new ExchangeFolderSync(getDataSource()).syncFolders(syncSettings, syncFactory.getPolicyKey(mbox), isDebugTraceOn);
            syncAllItems(tracker, isDebugTraceOn);
        } catch (HttpStatusException.UnauthorizedException x) {
            throw RemoteServiceException.AUTH_FAILURE("xsync", x);
        } catch (HttpStatusException.ForbiddenException x) {
            throw RemoteServiceException.AUTH_DENIED("xsync", x);
        } catch (HttpStatusException.BadRequestException x) {
            throw RemoteServiceException.PROTOCOL_EXCEPTION("xsync", x);
        } catch (HttpStatusException.NeedProvisioningException x) {
            if (!isRetry) {
                ZimbraLog.xsync.info("Server requiring Policy Provision");
                doProvision(isDebugTraceOn);
                sync(true);
            } else {
                throw ServiceException.FAILURE("Server rejecting policy key " + syncFactory.getPolicyKey(mbox), x);
            }
        } catch (HttpStatusException x) {
            throw ServiceException.FAILURE("xsync", x); //TODO
        } catch (CommandCallbackException x) {
            throw ServiceException.FAILURE("xsync", x); //TODO
        } catch (BinaryCodecException x) {
            throw RemoteServiceException.PROTOCOL_EXCEPTION("xsync", x);
        } catch (IOException x) {
            throw RemoteServiceException.CONNECT_FAILURE("xsync", x);
        }
    }

    private void doProvision(boolean isDebugTraceOn) throws ServiceException {
        ZimbraLog.xsync.info("Old policy key %s", syncFactory.getPolicyKey(mbox));
        String newPolicyKey = Provision.renewPolicy(syncSettings, isDebugTraceOn);
        ZimbraLog.xsync.info("New policy key %s", newPolicyKey);
        syncFactory.setPolicyKey(mbox, newPolicyKey);
    }

    private void syncAllItems(ChangeTracker tracker, boolean isDebugTraceOn) throws ServiceException, HttpStatusException, CommandCallbackException, BinaryCodecException, IOException {
        Map<Folder, ExchangeFolderMapping> syncOrder = new TreeMap<Folder, ExchangeFolderMapping>(new Comparator<Folder>() {
            public int compare(Folder o1, Folder o2) {
                int p1 = getFolderPriority(o1);
                int p2 = getFolderPriority(o2);
                if (p1 != p2)
                    return p1 - p2;
                return o1.getPath().compareTo(o2.getPath());
            }
        });

        ExchangeFolderMapping root = null;
        for (ExchangeFolderMapping efm : tracker.folderMappingsByClientId.values()) {
            try {
                if (efm.getItemId() == getDataSource().getFolderId())
                    root = efm;
                else if (efm.getItemId() != Mailbox.ID_FOLDER_DRAFTS && efm.getItemId() != Mailbox.ID_FOLDER_IM_LOGS)
                    syncOrder.put(mbox.getFolderById(new OperationContext(mbox), efm.getItemId()), efm);
            } catch (NoSuchItemException x) {
                //TODO: log
            }
        }

        for (Entry<Folder, ExchangeFolderMapping> entry : syncOrder.entrySet()) {
            try {
                Folder folder = entry.getKey();

                switch (folder.getDefaultView()) {
                case MailItem.TYPE_MESSAGE:
                case MailItem.TYPE_UNKNOWN:
                    if (LC.data_source_eas_sync_email.booleanValue())
                        break;
                    else
                        continue;
                case MailItem.TYPE_CONTACT:
                    if (LC.data_source_eas_sync_contacts.booleanValue())
                        break;
                    else
                        continue;
                case MailItem.TYPE_APPOINTMENT:
                    if (LC.data_source_eas_sync_calendar.booleanValue())
                        break;
                    else
                        continue;
                case MailItem.TYPE_TASK:
                    //if (LC.data_source_eas_sync_calendar.booleanValue())
                    if (false)
                        break;
                    else
                        continue;
                default:
                    continue;
                }

                ExchangeFolderMapping folderMapping = entry.getValue();
                syncFolderItems(folder, folderMapping, tracker, isDebugTraceOn);
                if (!folderMapping.isInitSyncDone()) {
                    folderMapping.setInitSyncDone();
                    folderMapping.update();
                }
            } catch (ResponseStatusException x) {
                //TODO
            }
        }
        tracker.syncComplete();

        if (!root.isInitSyncDone()) {
            root.setInitSyncDone();
            root.update();
        }
    }

    private void syncFolderItems(Folder folder, ExchangeFolderMapping folderMapping, ChangeTracker tracker, boolean isDebugTraceOn)
        throws ServiceException, HttpStatusException, ResponseStatusException, BinaryCodecException, IOException {
        String collectionId = folderMapping.getRemoteId();
        String syncKey = folderMapping.getSyncKey();
        ZimbraLog.xsync.debug("==== Sync of '%s' (ServerId=%s) starts (SyncKey=%s)", folder.getPath(), collectionId, syncKey);
        Sync sync = null;
        do {
            sync = CollectionSyncFactory.instance().createSyncCommand(folder, collectionId, syncKey, tracker);
            try {
                sync.doCommand(syncSettings, syncFactory.getPolicyKey(mbox), isDebugTraceOn);
            } catch (CommandCallbackException x) {
                ZimbraLog.xsync.warn("", x);
            }

            //TODO: need to deal with parsing error.  if the parsing of the same response fails twice in a row,
            //reduce windowSize to 1 and try again.  when that fails, skip over the single item and generate a report.

            for (String fetchId : sync.getFetchList()) {
                EmailFetch fetch = new EmailFetch(collectionId, sync.getServerSyncKey(), new MailboxEmailSync(folder, collectionId, tracker), fetchId);
                try {
                    fetch.doCommand(syncSettings, syncFactory.getPolicyKey(mbox), isDebugTraceOn);
                } catch (CommandCallbackException x) {
                    ZimbraLog.xsync.warn("", x);
                } catch (HttpStatusException x) {
                    //we are assuming server 500 error
                    ZimbraLog.xsync.warn("message fetching failed (ServerId=%s); skipping", fetchId, x);
                }
            }

            syncKey = sync.getServerSyncKey();
            folderMapping.setSyncKey(syncKey);
            folderMapping.update();
        } while (sync.getClientSyncKey().equals("0") || sync.hasMore());

        List<Integer> clientAdds = tracker.getClientAdds(folder.getId());
        List<Integer> clientChanges = tracker.getClientChanges(folder.getId());
        List<Integer> clientDeletes = tracker.getClientDeletes(folder.getId());
        if (clientAdds.size() + clientChanges.size() + clientDeletes.size() > 0) {
            sync = CollectionSyncFactory.instance().createSyncCommand(folder, collectionId, syncKey, tracker);
            MailboxCollectionSync mcs = CollectionSyncFactory.instance().createCollectionSync(folder, collectionId, tracker);
            for (int id : clientAdds)
                try {
                    sync.addClientAdd(mcs.newClientAdd(id));
                } catch (ServiceException x) {
                    ZimbraLog.xsync.warn("client add id=%d", id, x);
                }
            for (int id : clientChanges)
                try {
                    sync.addClientChange(mcs.newClientChange(id));
                } catch (ServiceException x) {
                    ZimbraLog.xsync.warn("client change id=%d", id, x);
                }
            for (int id : clientDeletes)
                sync.addClientDelete(mcs.newClientDelete(id));
            try {
                sync.doCommand(syncSettings, syncFactory.getPolicyKey(mbox), isDebugTraceOn);
            } catch (CommandCallbackException x) {
                ZimbraLog.xsync.warn("", x);
            }
            syncKey = sync.getServerSyncKey();
            folderMapping.setSyncKey(syncKey);
            folderMapping.update();
        }
        ZimbraLog.xsync.debug("==== Sync of '%s' (ServerId=%s) ends (SyncKey=%s)", folder.getPath(), collectionId, syncKey);
    }

    private int getFolderPriority(Folder f) {
        switch (f.getId()) {
        case Mailbox.ID_FOLDER_INBOX:
            return 0;
        case Mailbox.ID_FOLDER_CONTACTS:
            return 1;
        case Mailbox.ID_FOLDER_AUTO_CONTACTS:
            return 2;
        case Mailbox.ID_FOLDER_CALENDAR:
            return 3;
        case Mailbox.ID_FOLDER_TASKS:
            return 4;
        case Mailbox.ID_FOLDER_NOTEBOOK:
            return 5;
        case Mailbox.ID_FOLDER_BRIEFCASE:
            return 6;
        case Mailbox.ID_FOLDER_SENT:
            return 7;
        case Mailbox.ID_FOLDER_TRASH:
            return 8;
        case Mailbox.ID_FOLDER_SPAM:
            return 9;
        default:
            return 10;
        }
    }
}
