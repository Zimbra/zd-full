/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.util.ArrayList;
import java.util.Collection;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.datasource.DataSourceMapping;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;

public class ExchangeItemMapping extends DataSourceMapping {

    private static final String METADATA_KEY_REMOTE_PARENT_ID = "rpid";
    
    String remoteParentId;
    
    public ExchangeItemMapping(DataSource ds, DataSourceItem dsi) throws ServiceException {
        super(ds, dsi);
    }

    public ExchangeItemMapping(DataSource ds, int itemId) throws ServiceException {
        super(ds, itemId);
    }

    public ExchangeItemMapping(DataSource ds, String remoteId) throws ServiceException {
        super(ds, remoteId);
    }
    
    public ExchangeItemMapping(DataSource ds, int folderId, int itemId, String remoteId, String remoteParentId) throws ServiceException {
        super(ds, folderId, itemId, remoteId);
        setRemoteParentId(remoteParentId);
    }
    
    public void setRemoteParentId(String remoteParentId) {
        dsi.md.put(METADATA_KEY_REMOTE_PARENT_ID, remoteParentId);
        this.remoteParentId = remoteParentId;
    }
    
    @Override
    protected void parseMetaData() throws ServiceException {
        if (getFolderId() == ds.getFolderId())
            throw ServiceException.FAILURE(toString() + " is a folder and not a leaf item", null);
        remoteParentId = dsi.md.get(METADATA_KEY_REMOTE_PARENT_ID);
    }
    
    public String getRemoteParentId() {
        return remoteParentId;
    }
    
    @Override
    public String toString() {
        return "cid=" + getItemId() + ";sid=" + getRemoteId() + ";cpid=" + getFolderId() + ";rpid=" + remoteParentId;
    }
    
    @Override
    public void add() throws ServiceException {
        super.add();
        ZimbraLog.xsync.debug("added item mapping " + toString());
    }
    
    @Override
    public void update() throws ServiceException {
        super.update();
        ZimbraLog.xsync.debug("updated item mapping " + toString());
    }
    
    @Override
    public void delete() throws ServiceException {
        super.delete();
        ZimbraLog.xsync.debug("deleted item mapping " + toString());
    }
    
    public static Collection<ExchangeItemMapping> getMappings(DataSource ds, int folderId) throws ServiceException {
        Collection<DataSourceItem> dsMappings = DbDataSource.getAllMappingsInFolder(ds, folderId);
        Collection<ExchangeItemMapping> mappings = new ArrayList<ExchangeItemMapping>(dsMappings.size());
        for (DataSourceItem dsi : dsMappings) {
            mappings.add(new ExchangeItemMapping(ds, dsi));
        }
        return mappings;
    }
    
    public static Collection<ExchangeItemMapping> getMappings(DataSource ds, Collection<Integer> ids) throws ServiceException {
        Collection<DataSourceItem> dsMappings = DbDataSource.getMappings(ds, ids);
        Collection<ExchangeItemMapping> mappings = new ArrayList<ExchangeItemMapping>(dsMappings.size());
        for (DataSourceItem dsi : dsMappings) {
            if (dsi.folderId != ds.getFolderId()) //filter out folders as we only want leaf items
                mappings.add(new ExchangeItemMapping(ds, dsi));
        }
        return mappings;
    }
}
