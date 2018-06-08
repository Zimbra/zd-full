/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.DataSourceFolderMapping;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;

public class ExchangeFolderMapping extends DataSourceFolderMapping {

    private static final String METADATA_KEY_PARENT_ID = "pid";
    private static final String METADATA_KEY_REMOTE_PARENT_ID = "rpid";
    private static final String METADATA_KEY_DISPLAY_NAME = "name";
    private static final String METADATA_KEY_TYPE = "type";
    private static final String METADATA_KEY_SYNCKEY = "synckey";
    private static final String METADATA_KEY_MODSEQ = "modseq";
    private static final String METADATA_KEY_INITSYNC_DONE = "initdone";

    private int parentId;
    private String remoteParentId;
    private String displayName;
    private int type;

    private String syncKey;
    private int modSeq;

    private boolean initSyncDone;

    public ExchangeFolderMapping(DataSource ds, DataSourceItem dsi)
        throws ServiceException {
        super(ds, dsi);
    }

    public ExchangeFolderMapping(DataSource ds, int itemId)
        throws ServiceException {
        super(ds, itemId);
    }

    public ExchangeFolderMapping(DataSource ds, String remoteId)
        throws ServiceException {
        super(ds, remoteId);
    }

    public ExchangeFolderMapping(DataSource ds, int itemId, int parentId, String remoteId, String remoteParentId, String displayName, int type)
        throws ServiceException {
        super(ds, itemId, remoteId);
        setParentId(parentId);
        setRemoteParentId(remoteParentId);
        setDisplayName(displayName);
        setType(type);
        setSyncKey("0");
    }

    public void setParentId(int parentId) {
        dsi.md.put(METADATA_KEY_PARENT_ID, parentId);
        this.parentId = parentId;
    }

    public void setRemoteParentId(String remoteParentId) {
        dsi.md.put(METADATA_KEY_REMOTE_PARENT_ID, remoteParentId);
        this.remoteParentId = remoteParentId;
    }

    public void setDisplayName(String displayName) {
        dsi.md.put(METADATA_KEY_DISPLAY_NAME, displayName);
        this.displayName = displayName;
    }

    public void setType(int type) {
        dsi.md.put(METADATA_KEY_TYPE, type);
        this.type = type;
    }

    public void setSyncKey(String syncKey) {
        dsi.md.put(METADATA_KEY_SYNCKEY, syncKey);
        this.syncKey = syncKey;
    }

    public void setChangeId(int modSeq) {
        dsi.md.put(METADATA_KEY_MODSEQ, modSeq);
        this.modSeq = modSeq;
    }

    public void setInitSyncDone() {
        dsi.md.put(METADATA_KEY_INITSYNC_DONE, true);
        this.initSyncDone = true;
    }

    @Override
    protected void parseMetaData() throws ServiceException {
        parentId = (int)dsi.md.getLong(METADATA_KEY_PARENT_ID);
        remoteParentId = dsi.md.get(METADATA_KEY_REMOTE_PARENT_ID);
        displayName = dsi.md.get(METADATA_KEY_DISPLAY_NAME);
        type = (int)dsi.md.getLong(METADATA_KEY_TYPE);
        syncKey = dsi.md.get(METADATA_KEY_SYNCKEY, null);
        modSeq = (int)dsi.md.getLong(METADATA_KEY_MODSEQ, 0);
        initSyncDone = dsi.md.getBool(METADATA_KEY_INITSYNC_DONE, false);
    }

    public int getParentId() {
        return parentId;
    }

    public String getRemoteParentId() {
        return remoteParentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSyncKey() {
        return syncKey;
    }

    public int getChangeId() {
        return modSeq;
    }

    public boolean isInitSyncDone() {
        return initSyncDone;
    }

    @Override
    public String toString() {
        return "cid=" + getItemId() + ";sid=" + getRemoteId() + ";cpid=" + parentId + ";rpid=" + remoteParentId +
            ";name='" + displayName + "';type=" + type + ";synckey=" + syncKey + ";modseq=" + modSeq + ";initdone=" + initSyncDone;
    }

    @Override
    public void add() throws ServiceException {
        super.add();
        ZimbraLog.xsync.debug("added folder mapping " + toString());
    }

    @Override
    public void update() throws ServiceException {
        super.update();
        ZimbraLog.xsync.debug("updated folder mapping " + toString());
    }

    @Override
    public void delete() throws ServiceException {
        super.delete();
        ZimbraLog.xsync.debug("deleted folder mapping " + toString());
    }

    public static Map<Integer, ExchangeFolderMapping> getMappingsByClientId(Collection<ExchangeFolderMapping> folderMappings) {
        Map<Integer, ExchangeFolderMapping> map = new HashMap<Integer, ExchangeFolderMapping>();
        for (ExchangeFolderMapping efm : folderMappings)
            map.put(efm.getItemId(), efm);
        return map;
    }

    public static Map<String, ExchangeFolderMapping> getMappingsByServerId(Collection<ExchangeFolderMapping> folderMappings) {
        Map<String, ExchangeFolderMapping> map = new HashMap<String, ExchangeFolderMapping>();
        for (ExchangeFolderMapping efm : folderMappings)
            map.put(efm.getRemoteId(), efm);
        return map;
    }

    public static Collection<ExchangeFolderMapping> getFolderMappings(DataSource ds, int folderId) throws ServiceException {
        Collection<DataSourceItem> dsMappings = getMappings(ds, folderId);
        Collection<ExchangeFolderMapping> folderMappings = new ArrayList<ExchangeFolderMapping>(dsMappings.size());
        for (DataSourceItem dsi : dsMappings) {
            folderMappings.add(new ExchangeFolderMapping(ds, dsi));
        }
        return folderMappings;
    }
}
