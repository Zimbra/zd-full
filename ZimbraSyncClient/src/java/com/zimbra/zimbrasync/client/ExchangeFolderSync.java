/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;


import com.zimbra.zimbrasync.client.ChangeTracker.TrackerItemMove;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.FolderCreate;
import com.zimbra.zimbrasync.client.cmd.FolderDelete;
import com.zimbra.zimbrasync.client.cmd.FolderSync;
import com.zimbra.zimbrasync.client.cmd.FolderUpdate;
import com.zimbra.zimbrasync.client.cmd.HttpStatusException;
import com.zimbra.zimbrasync.client.cmd.MoveItems;
import com.zimbra.zimbrasync.client.cmd.ResponseStatusException;
import com.zimbra.zimbrasync.client.cmd.SyncSettings;
import com.zimbra.zimbrasync.client.cmd.FolderSync.FolderSyncAdd;
import com.zimbra.zimbrasync.client.cmd.FolderSync.FolderSyncDelete;
import com.zimbra.zimbrasync.client.cmd.FolderSync.FolderSyncUpdate;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;

public class ExchangeFolderSync {

    static class FolderClientInfo {
        int clientId;
        String clientPath;
    }

    static class FolderAdd extends FolderClientInfo {
        int clientParentId;
        String serverParentId;
        int serverType;
        String serverName;

        FolderAdd(Folder f, String serverParentId, int serverType) {
            clientId = f.getId();
            clientParentId = f.getFolderId();
            clientPath = f.getPath();
            this.serverParentId = serverParentId;
            this.serverType = serverType;
            serverName = f.getName();
        }
    }

    static class FolderMod extends FolderClientInfo {
        int clientParentId;
        String serverId;
        String serverParentId;
        String serverName;

        boolean isMoved; //moved
        boolean isRenamed; //renamed

        String oldServerParentId;
        String oldServerName;

        //due to client change
        FolderMod(Folder f, String serverId, String serverParentId) {
            clientId = f.getId();
            clientParentId = f.getFolderId();
            clientPath = f.getPath();
            this.serverId = serverId;
            this.serverParentId = serverParentId;
            serverName = f.getName();
        }

        //due to server change
        FolderMod(ExchangeFolderMapping efm, FolderSyncUpdate action) {
            clientId = efm.getItemId();
            serverId = efm.getRemoteId();
            serverParentId = action.parentId;
            serverName = action.displayName;
            oldServerParentId = efm.getRemoteParentId();
            oldServerName = efm.getDisplayName();

            if (!oldServerParentId.equals(serverParentId))
                isMoved = true;
            if (!oldServerName.equals(serverName))
                isRenamed = true;
        }

        void revertMove() {
            serverParentId = oldServerParentId;
            isMoved = false;
        }

        void revertRename() {
            serverName = oldServerName;
            isRenamed = false;
        }
    }

    static class FolderDel {
        int clientId;
        String serverId;
        String serverPath;

        FolderDel(ExchangeFolderMapping efm) {
            clientId = efm.getItemId();
            serverId = efm.getRemoteId();
        }
    }

    static class FolderClientChanges {
        private Set<FolderAdd> adds = new TreeSet<FolderAdd>(new Comparator<FolderClientInfo>() {
            public int compare(FolderClientInfo o1, FolderClientInfo o2) {
                return o1.clientPath.compareTo(o2.clientPath); //forward
            }
        });

        private Set<FolderMod> mods = new TreeSet<FolderMod>(new Comparator<FolderClientInfo>() {
            public int compare(FolderClientInfo o1, FolderClientInfo o2) {
                return o2.clientPath.compareTo(o1.clientPath); //reverse
            }
        });

        private List<FolderDel> dels = new ArrayList<FolderDel>();

        private Map<Integer, FolderAdd> addsByClientId = new HashMap<Integer, FolderAdd>();
        private Map<String, FolderMod> changesByServerId = new HashMap<String, FolderMod>();
        private Map<String, FolderDel> deletesByServerId = new HashMap<String, FolderDel>();

        private int changeId;

        FolderClientChanges(int changeId) {
            this.changeId = changeId;
        }

        int getChangeId() {
            return changeId;
        }

        void add(FolderAdd add) {
            adds.add(add);
            addsByClientId.put(add.clientId, add);
        }

        void add(FolderMod mod) {
            mods.add(mod);
            changesByServerId.put(mod.serverId, mod);
        }

        void add(FolderDel del) {
            dels.add(del);
            deletesByServerId.put(del.serverId, del);
        }

        boolean isAdded(int clientId) {
            return addsByClientId.get(clientId) != null;
        }

        boolean isMoved(String serverId) {
            FolderMod fc = changesByServerId.get(serverId);
            return fc != null && fc.isMoved;
        }

        boolean isRenamed(String serverId) {
            FolderMod fc = changesByServerId.get(serverId);
            return fc != null && fc.isRenamed;
        }

        boolean isDeleted(String serverId) {
            return deletesByServerId.containsKey(serverId);
        }

        boolean remove(int clientId) {
            if (removeClientCreate(clientId))
                return true;
            if (removeClientUpdate(clientId))
                return true;
            if (removeClientDelete(clientId))
                return true;
            return false;
        }

        boolean removeClientCreate(int clientId) {
            for (Iterator<FolderAdd> i = adds.iterator(); i.hasNext();) {
                FolderAdd fa = i.next();
                if (fa.clientId == clientId) {
                    i.remove();
                    return true;
                }
            }
            return false;
        }

        boolean removeClientUpdate(int clientId) {
            for (Iterator<FolderMod> i = mods.iterator(); i.hasNext();) {
                FolderMod fc = i.next();
                if (fc.clientId == clientId) {
                    i.remove();
                    changesByServerId.remove(fc.serverId);
                    return true;
                }
            }
            return false;
        }

        boolean removeClientDelete(int clientId) {
            for (Iterator<FolderDel> i = dels.iterator(); i.hasNext();) {
                FolderDel fd = i.next();
                if (fd.clientId == clientId) {
                    i.remove();
                    deletesByServerId.remove(fd.serverId);
                    return true;
                }
            }
            return false;
        }

        Collection<FolderAdd> getCreateList() {
            return adds;
        }

        Collection<FolderMod> getUpdateList() {
            return mods;
        }

        List<FolderDel> getDeleteList() {
            return dels;
        }
    }

    static class FolderChangeTracker {
        Mailbox mbox;
        DataSource ds;

        Collection<ExchangeFolderMapping> mappings;
        Map<Integer, ExchangeFolderMapping> mappingsByClientId;
        Map<String, ExchangeFolderMapping> mappingsByServerId;

        FolderChangeTracker(Mailbox mbox, DataSource ds) throws ServiceException {
            this.mbox = mbox;
            this.ds = ds;

            mappings = ExchangeFolderMapping.getFolderMappings(ds, ds.getFolderId());
            mappingsByClientId = ExchangeFolderMapping.getMappingsByClientId(mappings);
            mappingsByServerId = ExchangeFolderMapping.getMappingsByServerId(mappings);

            if (mappings.size() == 0)
                addSyncRelation(ds.getFolderId(), ds.getFolderId(), "0", "0", "ROOT", 0);
        }

        ExchangeFolderMapping getRoot() {
            return mappingsByServerId.get("0");
        }

        String getServerId(int clientId) {
            ExchangeFolderMapping efm = mappingsByClientId.get(clientId);
            assert efm != null : "getServerId(" + clientId + ")";
            return efm.getRemoteId();
        }

        int getClientId(String serverId) {
            ExchangeFolderMapping efm = mappingsByServerId.get(serverId);
            assert efm != null : "getClientId(" + serverId + ")";
            return efm.getItemId();
        }

        FolderMod getServerUpdate(FolderSyncUpdate update) {
            ExchangeFolderMapping efm = mappingsByServerId.get(update.serverId);
            assert efm != null : "remote_id=" + update.serverId;
            return new FolderMod(efm, update);
        }

        FolderClientChanges findClientChanges() throws ServiceException {
            FolderClientChanges clientChanges = new FolderClientChanges(mbox.getLastChangeID());
            List<Folder> folders = mbox.getFolderList(getContext(false), SortBy.NONE);
            Set<Integer> folderSet = new HashSet<Integer>();
            for (Folder f : folders) {
                if (f.getId() < Mailbox.FIRST_USER_ID || f.getId() == ds.getFolderId())
                    continue; //skip system folders and root

                ExchangeFolderMapping mapping = mappingsByClientId.get(f.getId());
                ExchangeFolderMapping parent = mappingsByClientId.get(f.getFolderId());
                String parentId = parent == null ? null : parent.getRemoteId();
                if (mapping == null) {
                    int serverType = getServerType(f);
                    if (serverType > 0)
                        clientChanges.add(new FolderAdd(f, parentId, serverType));
                } else {
                    boolean isRenamed = !mapping.getDisplayName().equals(f.getName());
                    boolean isMoved = !mapping.getRemoteParentId().equals(parentId);
                    if (isRenamed || isMoved) {
                        FolderMod mod = new FolderMod(f, mapping.getRemoteId(), parentId);
                        mod.isRenamed = isRenamed;
                        mod.isMoved = isMoved;
                        clientChanges.add(mod);
                    }
                    folderSet.add(f.getId());
                }
            }

            for (Entry<Integer, ExchangeFolderMapping> e : mappingsByClientId.entrySet()) {
                if (e.getKey() == ds.getFolderId() || e.getKey() < Mailbox.FIRST_USER_ID)
                    continue;
                if (!folderSet.contains(e.getKey())) {
                    FolderDel del = new FolderDel(e.getValue());
                    clientChanges.add(del);
                }
            }

            return clientChanges;
        }

        void addSyncRelation(int clientId, int clientParentId, String serverId, String serverParentId, String displayName, int type) throws ServiceException {
            ExchangeFolderMapping efm = new ExchangeFolderMapping(ds, clientId, clientParentId, serverId, serverParentId, displayName, type);
            efm.add();
            mappings.add(efm);
            mappingsByClientId.put(clientId, efm);
            mappingsByServerId.put(serverId, efm);
        }

        void updateSyncRelation(int clientId, int parentId, String serverParentId, String displayName) throws ServiceException {
            ExchangeFolderMapping emf = mappingsByClientId.get(clientId);
            emf.setParentId(parentId);
            emf.setRemoteParentId(serverParentId);
            emf.setDisplayName(displayName);
            emf.update();
        }

        void updateSyncRelation(int clientId, int parentId) throws ServiceException { //local move
            ExchangeFolderMapping emf = mappingsByClientId.get(clientId);
            emf.setParentId(parentId);
            emf.update();
        }

        void deleteSyncRelation(int clientId) throws ServiceException {
            ExchangeFolderMapping emf = mappingsByClientId.get(clientId);
            emf.delete();
            mappingsByClientId.remove(clientId);
            mappingsByServerId.remove(emf.getRemoteId());
            for (Iterator<ExchangeFolderMapping> i = mappings.iterator(); i.hasNext();) {
                if (i.next().getItemId() == clientId) {
                    i.remove();
                    break;
                }
            }
        }

        String getServerPath(String serverId) {
            ExchangeFolderMapping efm = mappingsByServerId.get(serverId);
            String remoteParentId = efm.getRemoteParentId();
            if (remoteParentId.equals("0"))
                return "/" + efm.getDisplayName();
            else
                return getServerPath(remoteParentId) + "/" + efm.getDisplayName();
        }

        /**
         * @param ancestor potential ancestor
         * @param descendant potential descendant
         * @return true if descendant is under ancestor
         */
        boolean isDescendantPerServer(int ancestor, int descendant) {
            if (ancestor == descendant)
                return true;
            int parentId = mappingsByClientId.get(descendant).getParentId();
            if (parentId == ds.getFolderId())
                return false;
            else if (parentId == ancestor)
                return true;
            else
                return isDescendantPerServer(ancestor, parentId);
        }

        Set<Integer> findAllDescendantsOnServer(int ancestor) {
            Set<Integer> result = new HashSet<Integer>();
            for (ExchangeFolderMapping efm : mappings) {
                int id = efm.getItemId();
                if (isDescendantPerServer(ancestor, id)) //including top node in question as well
                    result.add(id);
            }
            return result;
        }

        private OperationContext getContext(boolean markChanges) throws ServiceException {
            return new OperationContext(mbox);
        }
    }

    private static int getEquivClientSystemFolderId(DataSource ds, String displayName, String serverParentId, int type) {
        if (ds.getFolderId() == Mailbox.ID_FOLDER_USER_ROOT) {
            if (type == 12 && serverParentId.equals("0") && displayName.equals("Junk E-Mail"))
                return Mailbox.ID_FOLDER_SPAM;

            switch (type) {
            case 2:
                return Mailbox.ID_FOLDER_INBOX;
            case 3:
                return Mailbox.ID_FOLDER_DRAFTS;
            case 4:
                return Mailbox.ID_FOLDER_TRASH;
            case 5:
                return Mailbox.ID_FOLDER_SENT;
            case 6:
                return -1;
            case 7:
                return Mailbox.ID_FOLDER_TASKS;
            case 8:
                return Mailbox.ID_FOLDER_CALENDAR;
            case 9:
                return Mailbox.ID_FOLDER_CONTACTS;
            case 10:
                return Mailbox.ID_FOLDER_NOTEBOOK;
            case 11:
                return -1;
            }
        }
        return 0;
    }

    private static int getServerType(Folder f) {
        assert f.getId() > Mailbox.HIGHEST_SYSTEM_ID : "id=" + f.getId();
        switch (f.getDefaultView()) {
        case MailItem.TYPE_UNKNOWN:
            return 1;
        case MailItem.TYPE_MESSAGE:
        case MailItem.TYPE_CHAT:
            return 12;
        case MailItem.TYPE_APPOINTMENT:
            return 13;
        case MailItem.TYPE_CONTACT:
            return 14;
        case MailItem.TYPE_TASK:
            return 15;
        case MailItem.TYPE_WIKI:
            return 17;
        default:
            return -1;
        }
    }

    private static byte getDefaultViewByServerType(int type) {
        switch (type) {
        case 1:
            return MailItem.TYPE_UNKNOWN;
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
            return 0;
        case 12:
            return MailItem.TYPE_MESSAGE;
        case 13:
            return MailItem.TYPE_APPOINTMENT;
        case 14:
            return MailItem.TYPE_CONTACT;
        case 15:
            return MailItem.TYPE_TASK;
        case 17:
            return MailItem.TYPE_WIKI;
        default:
            return 0;
        }
    }

    private static final Set<String> RESERVED_FOLDER_NAMES = new HashSet<String>();
    static {
        RESERVED_FOLDER_NAMES.add("Inbox");
        RESERVED_FOLDER_NAMES.add("Trash");
        RESERVED_FOLDER_NAMES.add("Junk");
        RESERVED_FOLDER_NAMES.add("Sent");
        RESERVED_FOLDER_NAMES.add("Drafts");
        RESERVED_FOLDER_NAMES.add("Contacts");
        RESERVED_FOLDER_NAMES.add("Notebook");
        RESERVED_FOLDER_NAMES.add("Calendar");
        RESERVED_FOLDER_NAMES.add("Tasks");
        RESERVED_FOLDER_NAMES.add("Chats");
        RESERVED_FOLDER_NAMES.add("Briefcase");

        RESERVED_FOLDER_NAMES.add("Outbox");
        RESERVED_FOLDER_NAMES.add("Error Reports");
        RESERVED_FOLDER_NAMES.add("Notification Mountpoints");
    }

    private Mailbox mbox;
    private DataSource ds;

    public ExchangeFolderSync(DataSource ds) throws ServiceException {
        this.ds = ds;
        this.mbox = DataSourceManager.getInstance().getMailbox(ds);
    }

    protected OperationContext getContext(boolean markChanges) throws ServiceException {
        return ExchangeSyncFactory.getInstance().getContext(mbox, markChanges);
    }

    protected void markItemMoved(int id) throws ServiceException {}

    protected void markItemCreated(int id) throws ServiceException {}

    String getSyncKey() throws ServiceException {
        ExchangeFolderMapping root = null;
        try {
            root = new ExchangeFolderMapping(ds, "0");
        } catch (NoSuchItemException x) {}

        return root == null ? "0" : root.getSyncKey();
    }

    void setSyncKey(ExchangeFolderMapping root, int modSeq, String syncKey) throws ServiceException {
        boolean changed = false;
        if (modSeq > 0 && root.getChangeId() != modSeq) {
            root.setChangeId(modSeq);
            changed = true;
        }
        if (syncKey != null && !root.getSyncKey().equals(syncKey)) {
            root.setSyncKey(syncKey);
            changed = true;
        }
        if (changed)
            root.update();
    }

    private String uniqFolderName(String serverFolderName) {
        String uuid = '{' + UUID.randomUUID().toString() + '}';
        String newName = null;
        if (serverFolderName.length() + uuid.length() > MailItem.MAX_NAME_LENGTH)
            newName = serverFolderName.substring(0, MailItem.MAX_NAME_LENGTH - uuid.length()) + uuid;
        else
            newName = serverFolderName + uuid;
        return newName;
    }

    protected ChangeTracker syncFolders(SyncSettings syncSettings, String policyKey, boolean isDebugTraceOn) throws HttpStatusException, BinaryCodecException, ServiceException {
        try {
            String syncKey = getSyncKey();
            boolean retry = false;
            FolderSync fSyncCmd = null;
            do {
                retry = false;
                ZimbraLog.xsync.debug("==== FolderSync starts (SyncKey=%s)", syncKey);
                fSyncCmd = new FolderSync(syncKey);
                try {
                    fSyncCmd.doCommand(syncSettings, policyKey, isDebugTraceOn);
                } catch (ResponseStatusException x) {
                    if (x.isInvalidSyncKey()) {
                        ZimbraLog.xsync.warn("invalid sync key %s; retry from 0", syncKey);
                        syncKey = "0";
                        retry = true;
                    } else {
                        throw ServiceException.FAILURE("can't sync remote folders", x);
                        //TODO
                    }
                } catch (CommandCallbackException x) {
                    throw ServiceException.FAILURE("can't sync remote folders", x);
                    //TODO
                }
            } while (retry);

            FolderChangeTracker folderTracker = null;
            FolderClientChanges clientChanges = null;
            ChangeTracker itemTracker = null;
            synchronized (mbox) {
                folderTracker = new FolderChangeTracker(mbox, ds);
                clientChanges = folderTracker.findClientChanges();

                List<FolderSyncUpdate> folderUpdates = fSyncCmd.getUpdates();

                //apply server changes
                for (FolderSyncAdd syncAdd : fSyncCmd.getAdds()) {
                    ZimbraLog.xsync.debug("server new folder %s (ServerId=%s;ParentId=%s;Type=%d)",
                        syncAdd.displayName, syncAdd.serverId, syncAdd.parentId, syncAdd.type);

                    if (folderTracker.mappingsByServerId.containsKey(syncAdd.serverId)) {
                        ZimbraLog.sync.debug("mapping for server folder (ServerId=%s) already exists with client folder (id=%d)", syncAdd.serverId,
                            folderTracker.mappingsByServerId.get(syncAdd.serverId).getItemId());
                        folderUpdates.add(new FolderSyncUpdate(syncAdd.serverId, syncAdd.parentId, syncAdd.displayName, syncAdd.type)); //process as update
                        continue;
                    }

                    int clientId = getEquivClientSystemFolderId(ds, syncAdd.displayName, syncAdd.parentId, syncAdd.type);
                    int clientParentId = folderTracker.getClientId(syncAdd.parentId);
                    byte clientDefaultView = getDefaultViewByServerType(syncAdd.type);
                    if (clientId == 0 && clientDefaultView != 0 &&  //clientId==0 means non-system folder
                        !(clientParentId == Mailbox.ID_FOLDER_USER_ROOT && RESERVED_FOLDER_NAMES.contains(syncAdd.displayName))) {
                        boolean reloc = false;
                        if (clientChanges.isDeleted(syncAdd.parentId)) {
                            //the parent folder was deleted locally, so create the new folder under ds root
                            clientParentId = ds.getFolderId();
                            reloc = true;
                            ZimbraLog.xsync.debug("parent folder (ServerId=%s) was deleted from client. relocating folder %s to under root",
                                syncAdd.parentId, syncAdd.displayName);
                        }

                        Folder f = null;
                        try {
                            f = mbox.createFolder(getContext(false), syncAdd.displayName, clientParentId, clientDefaultView, 0, (byte)0, null);
                            ZimbraLog.xsync.info("new folder '%s' created (id=%d)", f.getPath(), f.getId());
                        } catch (MailServiceException x) {
                            if (x.getCode().equals(MailServiceException.ALREADY_EXISTS)) {
                                if (reloc) { //if collision due to reloc, we need to rename
                                    f = mbox.createFolder(getContext(false), uniqFolderName(syncAdd.displayName), clientParentId, clientDefaultView, 0, (byte)0, null);
                                    ZimbraLog.xsync.info("new folder '%s' (renamed due to collision) created (id=%d)", f.getPath(), f.getId());
                                } else { //if collision under same parent, merge with existing
                                    f = mbox.getFolderByName(getContext(false), clientParentId, syncAdd.displayName);
                                    if (clientChanges.removeClientCreate(f.getId()))
                                        ZimbraLog.xsync.info("new server folder '%s' merged with new client folder '%s' (id=%d)",
                                            syncAdd.displayName, f.getPath(), f.getId());
                                    else {
                                        ZimbraLog.xsync.warn("new server folder '%s' name collision with old local folder %s (id=%d); skipping",
                                            syncAdd.displayName, f.getPath(), f.getId());
                                        continue;
                                    }
                                }
                            } else {
                                throw x;
                            }
                        }
                        if (reloc) {
                            markItemMoved(f.getId());
                            clientChanges.add(new FolderMod(f, syncAdd.serverId, null)); //null will get replaced on push
                        }
                        clientId = f.getId();
                    }
                    if (clientId > 0)
                        folderTracker.addSyncRelation(clientId, clientParentId, syncAdd.serverId, syncAdd.parentId, syncAdd.displayName, syncAdd.type);
                }

                for (FolderSyncUpdate syncUpdate : folderUpdates) {
                    ZimbraLog.xsync.debug("server change on folder %s (ServerId=%s;ParentId=%s;Type=%d)",
                        syncUpdate.displayName, syncUpdate.serverId, syncUpdate.parentId, syncUpdate.type);
                    if (clientChanges.isDeleted(syncUpdate.serverId)) {
                        ZimbraLog.xsync.debug("server folder (ServerId=%s) change ignored as it's deleted from client", syncUpdate.serverId);
                        continue; //we'll push up the deletes later
                    }

                    FolderMod serverUpdate = folderTracker.getServerUpdate(syncUpdate);
                    if (clientChanges.isMoved(syncUpdate.serverId))
                        serverUpdate.revertMove(); //don't apply server move, push client move later
                    if (clientChanges.isRenamed(syncUpdate.serverId))
                        serverUpdate.revertRename(); //don't apply server rename, push client rename later

                    //do server move and/or rename
                    int clientParentId = -1;
                    boolean reloc = false;
                    if (serverUpdate.isMoved) {
                        if (clientChanges.isDeleted(serverUpdate.serverParentId) || clientChanges.isMoved(serverUpdate.serverParentId)) {
                            //parent was deleted or moved, move folder to ds root
                            //this is to avoid complication in case parent was moved under child on client
                            clientParentId = ds.getFolderId();
                            reloc = true;
                            ZimbraLog.xsync.debug("destination folder (ServerId=%s) was deleted or moved from client. relocating folder %s to under root",
                                serverUpdate.serverParentId, serverUpdate.serverName);
                        } else {
                            clientParentId = folderTracker.getClientId(serverUpdate.serverParentId);
                        }
                    }

                    boolean uniqed = false;
                    if (serverUpdate.isMoved || serverUpdate.isRenamed) {
                        try {
                            mbox.rename(getContext(false), serverUpdate.clientId, MailItem.TYPE_FOLDER, serverUpdate.serverName, clientParentId);
                        } catch (MailServiceException x) {
                            if (x.getCode().equals(MailServiceException.ALREADY_EXISTS)) {
                                mbox.rename(getContext(false), serverUpdate.clientId, MailItem.TYPE_FOLDER, uniqFolderName(serverUpdate.serverName), clientParentId);
                                uniqed = true;
                            } else {
                                throw x;
                            }
                        }
                        Folder f = mbox.getFolderById(getContext(false), serverUpdate.clientId);
                        ZimbraLog.xsync.info("folder (id=%d) updated to '%s'", f.getId(), f.getPath());
                        if (reloc || uniqed) {
                            markItemMoved(serverUpdate.clientId); //maybe should mark as renamed as well
                            clientChanges.add(new FolderMod(f, serverUpdate.serverId, null)); //null will get replaced on push
                        }
                        folderTracker.updateSyncRelation(folderTracker.getClientId(syncUpdate.serverId), clientParentId, serverUpdate.serverParentId, serverUpdate.serverName);
                    }
                }

                //apply server deletes

                itemTracker = ExchangeSyncFactory.getInstance().getClientChanges(ds, folderTracker.mappingsByClientId);

                //ZCS returns individual folders in the delete list, whereas Exchange returns only the top nodes.
                //To be consistent, we go through the list and retain only the top nodes.
                Set<Integer> serverDeletes = new HashSet<Integer>();
                OUTER: for (FolderSyncDelete syncDelete : fSyncCmd.getDeletes()) {
                    int delId = folderTracker.getClientId(syncDelete.serverId);
                    for (Iterator<Integer> i = serverDeletes.iterator(); i.hasNext();) {
                        int folderId = i.next();
                        if (folderTracker.isDescendantPerServer(folderId, delId))
                            continue OUTER; //already covered
                        else if (folderTracker.isDescendantPerServer(delId, folderId))
                            i.remove();
                    }
                    serverDeletes.add(delId);
                }

                for (int delId : serverDeletes) {
                    Folder delete = null;
                    try {
                        delete = mbox.getFolderById(getContext(false), delId);
                    } catch (NoSuchItemException x) {
                        ZimbraLog.xsync.warn("server delete of folder (id=%d) does not exit; skipping", delId);
                        continue;
                    }
                    ZimbraLog.xsync.debug("server delete of folder '%s' (id=%d)", delete.getPath(), delete.getId());

                    //find everything that got created or moved under deleted folder as well as their intermediate ancestors
                    //we use tracker's server mappings to figure out what was under a folder when it was deleted on server
                    //at this point we have applied server changes to the tracker's mapping so it has the correct perspective.
                    Set<Folder> conflicts = new HashSet<Folder>();
                    String deleteServerId = folderTracker.getServerId(delete.getId());
                    for (FolderAdd add : clientChanges.getCreateList()) {
                        Folder f = mbox.getFolderById(getContext(false), add.clientId);
                        if (delete.isDescendant(f)) {
                            conflicts.add(f);
                            add.serverParentId = null; //null it out as it may have to be recreated
                            ZimbraLog.xsync.debug("new local folder %s (id=%d) was created under remotely deleted folder (ServerId=%s)", add.clientPath, add.clientId, deleteServerId);
                        }
                    }
                    for (FolderMod mod : clientChanges.getUpdateList()) {
                        Folder f = mbox.getFolderById(getContext(false), mod.clientId);
                        if (mod.isMoved && delete.isDescendant(f) && !folderTracker.isDescendantPerServer(delete.getId(), mod.clientId)) { //was not a descendant but is now
                            conflicts.add(f);
                            mod.serverParentId = null; //null it out as it may have to be recreated
                            ZimbraLog.xsync.debug("local folder %s (id=%d) was moved under remotely deleted folder (ServerId=%s)", mod.clientPath, mod.clientId, deleteServerId);
                        }
                    }

                    //We dealt with new/moved folders into remote delete, and now we deal with leaf items
                    List<Folder> tree = delete.getSubfolderHierarchy(); // tree includes top node
                    for (Folder f : tree) {
                        if (itemTracker.getClientAdds(f.getId()).size() > 0) {
                            conflicts.add(f);
                        } else {
                            List<TrackerItemMove> moves = itemTracker.getClientMovesIntoFolder(f.getId());
                            for (TrackerItemMove move : moves) { //need to see if it was under top node being deleted
                                if (!folderTracker.isDescendantPerServer(delete.getId(), move.srcFldId)) {
                                    conflicts.add(f);
                                    break;
                                }
                            }
                        }
                    }

                    for (Folder conflict : conflicts) {
                        if (conflict != delete) {
                            Folder f = (Folder)conflict.getParent();
                            while (f != delete) {
                                conflicts.add(f);
                                f = (Folder)f.getParent();
                            }
                        }
                    }
                    if (conflicts.size() > 0)
                        conflicts.add(delete); //top node itself must be also conflict

                    Set<Integer> subDeletes = folderTracker.findAllDescendantsOnServer(delete.getId());
                    Set<Folder> reverseTree = new TreeSet<Folder>(new Comparator<Folder>() { //reserve path order so we delete one at a time
                        public int compare(Folder o1, Folder o2) {
                            return o2.getPath().compareTo(o1.getPath());
                        }
                    });
                    for (Folder f : tree) {
                        if (!conflicts.contains(f)) {
                            reverseTree.add(f);
                            subDeletes.remove(f.getId());
                        } else if (subDeletes.contains(f.getId())) {
                            markItemCreated(f.getId());
                            folderTracker.deleteSyncRelation(f.getId());
                            String serverParentId = (serverDeletes.contains(f.getFolderId()) || subDeletes.contains(f.getFolderId())) ?
                                null : folderTracker.getServerId(f.getFolderId());
                            clientChanges.add(new FolderAdd(f, serverParentId, getServerType(f)));
                            subDeletes.remove(f.getId());
                            ZimbraLog.xsync.debug("folder '%s' (id=%d) was deleted on server, but will be recreated due to conflict", f.getPath(), f.getId());

                            for (TrackerItemMove move : itemTracker.getClientMovesIntoFolder(f.getId()))
                                move.setRemoteDstFldId(null); //null out the remote dst folder ID so that later it can be set after remote folder is recreated
                        }
                    }

                    //if there are left over in subDeletes, those were the folders that got moved out from top node locally. we need to delete them as well.
                    for (int id : subDeletes) {
                        Folder f = mbox.getFolderById(getContext(false), id);
                        reverseTree.add(f);
                        ZimbraLog.xsync.debug("folder '%s' (id=%d) got moved out of remotely deleted '%s' (id=%d) but will still be deleted", f.getPath(), id, delete.getPath(), delete.getId());
                    }

                    List<Integer> movedOutList = new ArrayList<Integer>();
                    //do the actual deletion
                    for (Folder f : reverseTree) {
                        int id = f.getId();
                        String path = f.getPath();
                        mbox.delete(getContext(false), id, f.getType());
                        folderTracker.deleteSyncRelation(id);
                        clientChanges.remove(id);
                        ZimbraLog.xsync.info("folder '%s' (id=%d) deleted", path, id);

                        List<TrackerItemMove> movedOut = itemTracker.getClientMovesFromFolder(id);
                        for (TrackerItemMove move : movedOut) {
                            movedOutList.add(move.id);
                            ZimbraLog.xsync.debug("item (id=%d) got moved out of remotely deleted folder (id=%d) but will still be deleted", move.id, move.srcFldId);
                        }
                    }

                    //finally, we need to delete leaf items that were moved out from deleted folders as they were deleted on the server
                    if (movedOutList.size() > 0) {
                        int[] movedOut = new int[movedOutList.size()];
                        for (int i = 0; i < movedOut.length; ++i)
                            movedOut[i] = movedOutList.get(i);
                        mbox.delete(getContext(false), movedOut, MailItem.TYPE_UNKNOWN, null);
                        ZimbraLog.xsync.debug("items %s deleted", movedOutList.toString());
                    }
                }

                syncKey = fSyncCmd.getServerSyncKey();
                setSyncKey(folderTracker.getRoot(), 0, syncKey); //don't update modseq yet, since client->server push can still fail
            }

            //push up locally created folders
            for (FolderAdd add : clientChanges.getCreateList()) {
                if (add.serverParentId == null) {
                    add.serverParentId = folderTracker.getServerId(add.clientParentId); //hopefully by now it's created and tracked since we are doing top down
                    assert add.serverParentId != null : "parent_id=" + add.clientParentId;
                }
                FolderCreate fCreateCmd = new FolderCreate(syncKey, add.serverParentId, add.serverName, add.serverType);
                try {
                    fCreateCmd.doCommand(syncSettings, policyKey, isDebugTraceOn);
                    ZimbraLog.xsync.info("created remoted folder %s (ServerId=%s)", add.serverName, fCreateCmd.getServerId());
                    folderTracker.addSyncRelation(add.clientId, add.clientParentId, fCreateCmd.getServerId(), add.serverParentId, add.serverName, add.serverType);
                    syncKey = fCreateCmd.getServerSyncKey();
                    setSyncKey(folderTracker.getRoot(), 0, syncKey);
                } catch (ResponseStatusException x) {
                    throw ServiceException.FAILURE("can't create remote folder " + add.serverName, x);
                    //TODO: handle command failure
                } catch (CommandCallbackException x) {
                    throw ServiceException.FAILURE("can't create remote folder " + add.serverName, x);
                    //TODO: handle command failure
                }
            }

            //push up local updates
            for (FolderMod mod : clientChanges.getUpdateList()) {
                if (mod.serverParentId == null) {
                    mod.serverParentId = folderTracker.getServerId(mod.clientParentId); //hopefully by now it's created and tracked since we are doing top down
                    assert mod.serverParentId != null : "parent_id=" + mod.clientParentId;
                }
                FolderUpdate fUpdateCmd = new FolderUpdate(syncKey, mod.serverId, mod.serverParentId, mod.serverName);
                try {
                    fUpdateCmd.doCommand(syncSettings, policyKey, isDebugTraceOn);
                    ZimbraLog.xsync.info("updated remoted folder %s (ServerId=%s)", mod.serverName, mod.serverId);
                    folderTracker.updateSyncRelation(mod.clientId, mod.clientParentId, mod.serverParentId, mod.serverName);
                    syncKey = fUpdateCmd.getServerSyncKey();
                    setSyncKey(folderTracker.getRoot(), 0, syncKey);
                } catch (ResponseStatusException x) {
                    throw ServiceException.FAILURE("can't update remote folder " + mod.serverId, x);
                    //TODO: handle command failure
                } catch (CommandCallbackException x) {
                    throw ServiceException.FAILURE("can't update remote folder " + mod.serverId, x);
                    //TODO: handle command failure
                }
            }

            //we do MoveItems before we push up local folder deletes
            if (itemTracker.getClientMoves() != null && itemTracker.getClientMoves().size() > 0) {
                for (TrackerItemMove move : itemTracker.moves.values())
                    if (move.getRemoteDstFldId() == null) {//was a new folder that got just pushed up
                        ExchangeFolderMapping efm = itemTracker.folderMappingsByClientId.get(move.dstFldId);
                        assert efm != null : "dst_id=" + move.dstFldId; //hopefully by now it's pushed up
                        move.setRemoteDstFldId(efm.getRemoteId());
                    }
                ZimbraLog.xsync.debug("MoveItems %d items", itemTracker.getClientMoves().size());
                try {
                    new MoveItems(itemTracker.getClientMoves(), itemTracker).doCommand(syncSettings, policyKey, isDebugTraceOn);
                } catch (ResponseStatusException x) {
                    assert false; //MoveItems shouldn't throw this
                } catch (CommandCallbackException x) {
                    throw ServiceException.FAILURE("", x);
                    //TODO: handle command failure
                }
            }

            //sort the delete list by server paths. recomputing at last minute because we want to apply all server folder moves first
            List<FolderDel> delList = clientChanges.getDeleteList();
            for (FolderDel del : delList)
                del.serverPath = folderTracker.getServerPath(del.serverId);
            Collections.sort(delList, new Comparator<FolderDel>() {
                public int compare(FolderDel o1, FolderDel o2) {
                    return o1.serverPath.compareTo(o2.serverPath);
                }
            });
            for (FolderDel del : clientChanges.getDeleteList()) {
                FolderDelete fDeleteCmd = new FolderDelete(syncKey, del.serverId);
                try {
                    fDeleteCmd.doCommand(syncSettings, policyKey, isDebugTraceOn);
                    ZimbraLog.xsync.info("deleted remoted folder %s (ServerId=%s)", del.serverPath, del.serverId);
                    folderTracker.deleteSyncRelation(del.clientId);
                    syncKey = fDeleteCmd.getServerSyncKey();
                    setSyncKey(folderTracker.getRoot(), 0, syncKey);
                } catch (ResponseStatusException x) {
                    if (x.isFolderNotFound())
                        ZimbraLog.xsync.warn("can't delete remote folder %s (ServerId=%s); not found; possibly due to FolderSync reset", del.serverPath, del.serverId, x);
                    else
                        throw ServiceException.FAILURE("can't delete remote folder " + del.serverPath + "(ServerId=" + del.serverId + ")", x);
                    //TODO: handle command failure
                } catch (CommandCallbackException x) {
                    throw ServiceException.FAILURE("can't delete remote folder " + del.serverPath + "(ServerId=" + del.serverId + ")", x);
                    //TODO: handle command failure
                }
            }

            setSyncKey(folderTracker.getRoot(), clientChanges.getChangeId(), syncKey);
            ZimbraLog.xsync.debug("==== FolderSync ends (SyncKey=%s)", syncKey);
            itemTracker.folderSyncComplete();
            return itemTracker;
        } catch (IOException x) {
            throw ServiceException.FAILURE("", x);
        }
    }
}
