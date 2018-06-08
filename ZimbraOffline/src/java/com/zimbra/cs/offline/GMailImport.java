/*
 * 
 */
package com.zimbra.cs.offline;

import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.DataSource;
import com.zimbra.common.service.ServiceException;

import java.util.List;
import java.util.ArrayList;

public class GMailImport implements DataSource.DataImport {
    private OfflineImport imapImport;
    private OfflineImport gabImport;
    private OfflineImport calDavImport;

    public GMailImport(OfflineDataSource ds) throws ServiceException {
        imapImport = OfflineImport.imapImport(ds);
        if (ds.isContactSyncEnabled()) {
            gabImport = OfflineImport.gabImport(ds.getContactSyncDataSource());
        }
        if (ds.isCalendarSyncEnabled()) {
            calDavImport = OfflineImport.gcalImport(ds.getCalendarSyncDataSource());
        }
    }

    public void test() throws ServiceException {
        if (gabImport != null) {
            gabImport.test();
        }
        if (calDavImport != null) {
            calDavImport.test();
        }
        imapImport.test();
    }
    
    public void importData(List<Integer> folderIds, boolean fullSync)
        throws ServiceException {
        List<ServiceException> errors = new ArrayList<ServiceException>();
        try {
            imapImport.importData(folderIds, fullSync);
        } catch (ServiceException e) {
            errors.add(e);
        }                               
        if (gabImport != null) {
            try {
                gabImport.importData(null, fullSync);
            } catch (ServiceException e) {
                errors.add(e);
            }
        }
        if (calDavImport != null) {
            try {
                calDavImport.importData(null, fullSync);
            } catch (ServiceException e) {
                errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            throw errors.get(0);
        }
    }
}

