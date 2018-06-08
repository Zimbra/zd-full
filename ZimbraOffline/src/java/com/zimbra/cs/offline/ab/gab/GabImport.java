/*
 * 
 */
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.mailbox.SyncExceptionHandler;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.LocalData;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;

import java.util.List;

public class GabImport implements DataSource.DataImport {
    private final OfflineDataSource ds;
    private SyncSession session;

    private static final Log LOG = OfflineLog.gab;

    private static final String ERROR = "Google address book synchronization failed";

    public GabImport(DataSource ds) {
        this.ds = (OfflineDataSource) ds;
    }

    public void test() throws ServiceException {
        session = new SyncSession(ds);
    }

    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
         // Only sync contacts if full sync or there are local contact changes
        if (!fullSync && !new LocalData(ds).hasLocalChanges()) {
            return;
        }
        LOG.info("Importing contacts for data source '%s'", ds.getName());
        DataSourceManager.getInstance().getMailbox(ds).beginTrackingSync();
        if (session == null) {
            session = new SyncSession(ds);
        }
        try {
            session.sync();
        } catch (Exception e) {
            if (!SyncExceptionHandler.isRecoverableException(null, 1, ERROR, e)) {
                ds.reportError(Mailbox.ID_FOLDER_CONTACTS, ERROR, e);
            }
        }
        LOG.info("Finished importing contacts for data source '%s'", ds.getName());
    }
}
