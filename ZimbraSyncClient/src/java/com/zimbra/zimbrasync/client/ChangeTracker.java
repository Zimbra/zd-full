/*
 * 
 */
/**
 * 
 */
package com.zimbra.zimbrasync.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.zimbrasync.client.MailboxCollectionSync.MailItemSyncState;
import com.zimbra.zimbrasync.client.cmd.CommandCallbackException;
import com.zimbra.zimbrasync.client.cmd.MoveItems.ItemMove;
import com.zimbra.zimbrasync.client.cmd.MoveItems.ItemMoveResponseCallback;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientItem;
import com.zimbra.zimbrasync.client.cmd.Sync.SyncError;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd.ClientAddResponseCallback;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange.ClientChangeResponseCallback;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientDelete.ClientDeleteResponseCallback;

public class ChangeTracker implements ItemMoveResponseCallback, ClientAddResponseCallback, ClientChangeResponseCallback, ClientDeleteResponseCallback {
    protected Mailbox mbox;
    protected DataSource ds;
    
    protected Map<Integer, ExchangeItemMapping> mappingByClientId = new HashMap<Integer, ExchangeItemMapping>();
    protected Map<String, ExchangeItemMapping> mappingByServerId = new HashMap<String, ExchangeItemMapping>();
    
    protected Map<Integer, ExchangeFolderMapping> folderMappingsByClientId;
    
    protected Map<Integer, List<Integer>> addsByFolderId = new HashMap<Integer, List<Integer>>();
    protected Map<Integer, List<Integer>> modsByFolderId = new HashMap<Integer, List<Integer>>();
    protected Map<Integer, List<Integer>> deletesByFoderId = new HashMap<Integer, List<Integer>>();
    
    private static final List<Integer> emptyList = new ArrayList<Integer>(0);
    
    private static List<Integer> getIntListByKey(Map<Integer, List<Integer>> map, int key) {
        List<Integer> values = map.get(key);
        return values != null ? values : emptyList;
    }
    
    private static void addNewValueToIntListMap(Map<Integer, List<Integer>> map, int key, int value) {
        List<Integer> list = map.get(key);
        if (list == null) {
            list = new ArrayList<Integer>();
            map.put(key, list);
        }
        list.add(value);
    }
    
    public static class TrackerItemMove extends ItemMove {
        protected int id;
        protected int srcFldId;
        protected int dstFldId;
        
        public TrackerItemMove(String remoteId, String remoteSrcFldId, String remoteDstFldId, int id, int srcFldId, int dstFldId) {
            super(remoteId, remoteSrcFldId, remoteDstFldId);
            this.id = id;
            this.srcFldId = srcFldId;
            this.dstFldId = dstFldId;
        }
    }
    
    protected Map<String, TrackerItemMove> moves = new HashMap<String, TrackerItemMove>();
    protected Map<Integer, List<TrackerItemMove>> movesBySrcFolderId = new HashMap<Integer, List<TrackerItemMove>>();
    protected Map<Integer, List<TrackerItemMove>> movesByDstFolderId = new HashMap<Integer, List<TrackerItemMove>>();
    
    private static List<TrackerItemMove> getMovesByKey(Map<Integer, List<TrackerItemMove>> map, int key) {
        List<TrackerItemMove> list = map.get(key);
        if (list == null) {
            list = new ArrayList<TrackerItemMove>();
            map.put(key, list);
        }
        return list;
    }
    
    protected int cutoffChangeId;
    
    public ChangeTracker(DataSource ds, Map<Integer, ExchangeFolderMapping> folderMappingsByClientId) throws ServiceException {
        this.ds = ds;
        this.folderMappingsByClientId = folderMappingsByClientId;
        mbox = DataSourceManager.getInstance().getMailbox(ds);
        
        synchronized (mbox) {
            cutoffChangeId = mbox.getLastChangeID();
            findClientChanges();
        }
    }
    
    public DataSource getDataSource() {
        return ds;
    }
    
    protected void addItemMappings(Collection<ExchangeItemMapping> mappings) throws ServiceException {
        for (ExchangeItemMapping eim : mappings) {
            mappingByClientId.put(eim.getItemId(), eim);
            mappingByServerId.put(eim.getRemoteId(), eim);
        }
    }
    
    protected void findClientChanges() throws ServiceException {
        //TODO:
        assert false;
    }
    
    protected void addItemMove(TrackerItemMove move) {
        moves.put(move.getRemoteId(), move);
        getMovesByKey(movesBySrcFolderId, move.srcFldId).add(move);
        getMovesByKey(movesByDstFolderId, move.dstFldId).add(move);
    }
    
    public List<Integer> getClientAdds(int folderId) {
        return getIntListByKey(addsByFolderId, folderId);
    }
    
    public List<Integer> getClientChanges(int folderId) {
        return getIntListByKey(modsByFolderId, folderId);
    }

    public List<Integer> getClientDeletes(int folderId) {
        return getIntListByKey(deletesByFoderId, folderId);
    }
    
    protected void addClientAdd(int folderId, int itemId) {
        addNewValueToIntListMap(addsByFolderId, folderId, itemId);
    }
    
    protected void addClientChange(int folderId, int itemId) {
        addNewValueToIntListMap(modsByFolderId, folderId, itemId);
    }

    protected void addClientDelete(int folderId, int itemId) {
        addNewValueToIntListMap(deletesByFoderId, folderId, itemId);
    }
    
    public Collection<? extends ItemMove> getClientMoves() {
        return moves.values();
    }
    
    public List<TrackerItemMove> getClientMovesFromFolder(int folderId) {
        return getMovesByKey(movesBySrcFolderId, folderId);
    }
    
    public List<TrackerItemMove> getClientMovesIntoFolder(int folderId) {
        return getMovesByKey(movesByDstFolderId, folderId);
    }
    
    public ExchangeItemMapping getMapping(String remoteId) throws ServiceException {
        ExchangeItemMapping eim = mappingByServerId.get(remoteId);
        if (eim == null) {
            try {
                eim = new ExchangeItemMapping(ds, remoteId);
                mappingByServerId.put(remoteId, eim);
                mappingByClientId.put(eim.getItemId(), eim);
            } catch (NoSuchItemException x) {}
        }
        return eim;
    }
    
    public void removeMapping(ExchangeItemMapping eim) {
        mappingByClientId.remove(eim.getItemId());
        mappingByServerId.remove(eim.getRemoteId());
    }
   
    //implements ItemMoveResponseCallback
    public void itemMoved(String srcId, String dstId) throws CommandCallbackException {
        ZimbraLog.xsync.info("moved server item from ServerId=%s to ServerId=%s", srcId, dstId);
        TrackerItemMove move = moves.get(srcId);
        assert move != null : "src_id=" + srcId;
        ExchangeItemMapping eim = mappingByServerId.get(srcId);
        assert eim != null : "src_id=" + srcId;
        eim.setRemoteId(dstId);
        eim.setFolderId(move.dstFldId);
        eim.setRemoteParentId(move.getRemoteDstFldId());
        try {
            eim.update();
            clearItemMoved(eim.getItemId(), move.dstFldId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    //implements ItemMoveResponseCallback
    public void itemMoveError(String srcId, ItemMoveError error) throws CommandCallbackException {
        //TODO
    }
   
    //template
    protected void clearItemMoved(int id, int folderId) throws ServiceException {}
    
    //implements ClientAddResponseCallback
    public void itemAdded(AirSyncClientItem clientItem, String serverId) throws CommandCallbackException {
        ZimbraLog.xsync.info("added server item (ServerId=%s)", serverId);
        try {
            MailItemSyncState miss = (MailItemSyncState)clientItem;
            ExchangeItemMapping eim = new ExchangeItemMapping(ds, miss.folderId, miss.id, serverId, folderMappingsByClientId.get(miss.folderId).getRemoteId());
            eim.add();
            clearItemAdded(miss.id, miss.changeId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    //implements ClientAddResponseCallback
    public void itemAddError(AirSyncClientItem clientItem, SyncError error) throws CommandCallbackException {
        // TODO Auto-generated method stub
    }
    
    //template
    protected void clearItemAdded(int id, int changeId) throws ServiceException {}
    
    //implements ClientChangeResponseCallback
    public void itemChanged(AirSyncClientItem clientItem) throws CommandCallbackException {
        try {
            MailItemSyncState miss = (MailItemSyncState)clientItem;
            ExchangeItemMapping eim = mappingByClientId.get(miss.id);
            ZimbraLog.xsync.info("changed server item (ServerId=%s)", eim.getRemoteId());
            clearItemChanged(miss.id, miss.changeId);
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    //implements ClientChangeResponseCallback
    public void itemChangeError(AirSyncClientItem clientItem, SyncError error) throws CommandCallbackException {
        // TODO Auto-generated method stub
    }
    
    //template
    protected void clearItemChanged(int id, int changeId) throws ServiceException {}
    
    //implements ClientDeleteResponseCallback
    public void itemDeleted(String serverId) throws CommandCallbackException {
        ZimbraLog.xsync.info("deleted server item (ServerId=%s)", serverId);
        try {
            ExchangeItemMapping eim = mappingByServerId.get(serverId);
            clearItemDeleted(eim.getItemId());
            removeMapping(eim);
            eim.delete();
        } catch (ServiceException x) {
            throw new CommandCallbackException(x);
        }
    }
    
    //implements ClientDeleteResponseCallback
    public void itemDeleteError(String serverId, SyncError error) throws CommandCallbackException {
        // TODO Auto-generated method stub
    }
    
    //template
    protected void clearItemDeleted(int id) throws ServiceException {}
   
    public void folderSyncComplete() throws ServiceException {}
    
    public void syncComplete() throws ServiceException {}
    
    public OperationContext getContext(boolean markChanges) throws ServiceException {
        return ExchangeSyncFactory.getInstance().getContext(mbox, markChanges);
    }
    
    private static byte[] supportedTypes = new byte[] {
        MailItem.TYPE_APPOINTMENT,
        MailItem.TYPE_CONTACT,
        //MailItem.TYPE_DOCUMENT,
        MailItem.TYPE_MESSAGE,
        MailItem.TYPE_TASK,
        //MailItem.TYPE_WIKI
    };
    
    protected byte[] getSupportedTypes() {
        return supportedTypes;
    }
}