/*
 * 
 */
package com.zimbra.cs.offline;

import java.io.IOException;
import java.io.InputStream;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.ExchangeHelper;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.zimbrasync.client.ExchangeSyncFactory;
import com.zimbra.zimbrasync.client.cmd.HttpStatusException;
import com.zimbra.zimbrasync.client.cmd.Request;

public class OfflineExchangeHelper implements ExchangeHelper {
    private Mailbox mbox;
    private DataSource ds;
    private ExchangeSyncFactory syncFactory;
    
    public OfflineExchangeHelper(Mailbox mbox, DataSource ds) {
        this.mbox = mbox;
        this.ds = ds;
        syncFactory = ExchangeSyncFactory.getInstance();
    }

    public void doSendMail(InputStream in, long size, boolean saveToSent) throws ServiceException, IOException {
        try {
            new Request(syncFactory.getSyncSettings(ds), syncFactory.getPolicyKey(mbox)).doSendMail(in, size, saveToSent);
        } catch (HttpStatusException x) {
            //TODO:
            throw ServiceException.FAILURE(x.getMessage(), x);
        }
    }
}