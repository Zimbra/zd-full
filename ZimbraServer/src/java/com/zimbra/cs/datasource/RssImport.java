/*
 * 
 */
package com.zimbra.cs.datasource;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.DataSource.DataImport;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;

/**
 * Imports data for RSS and remote calendar folders.
 */
public class RssImport implements DataImport {

    private DataSource mDataSource;
    
    public RssImport(DataSource ds) {
        mDataSource = ds;
    }
    
    public void importData(List<Integer> folderIds, boolean fullSync) throws ServiceException {
        Mailbox mbox = DataSourceManager.getInstance().getMailbox(mDataSource);
        int folderId = mDataSource.getFolderId();
        try {
            mbox.getFolderById(null, folderId);
            mbox.synchronizeFolder(null, folderId);
        } catch (NoSuchItemException e) {
            ZimbraLog.datasource.info("Folder %d was deleted.  Deleting data source %s.",
                folderId, mDataSource.getName());
            mbox.getAccount().deleteDataSource(mDataSource.getId());
        }
    }

    public void test()
    throws ServiceException {
        Mailbox mbox = DataSourceManager.getInstance().getMailbox(mDataSource);
        int folderId = mDataSource.getFolderId();
        
        Folder folder = mbox.getFolderById(null, folderId);
        String urlString = folder.getUrl();
        if (StringUtil.isNullOrEmpty(urlString)) {
            throw ServiceException.FAILURE("URL not specified for folder " + folder.getPath(), null);
        }
        
        InputStream in = null;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            in = conn.getInputStream();
            in.read();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Data source test failed.", e);
        } finally {
            ByteUtil.closeStream(in);
        }
    }

}
