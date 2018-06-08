/*
 * 
 */
package com.zimbra.zimbrasync.client;

import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Metadata;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.util.ZimbraApplication;
import com.zimbra.zimbrasync.client.cmd.SyncSettings;

public class ExchangeSyncFactory {
    private static final String XSYNC_SECTION = "xsync";
    private static final String XSYNC_POLICYKEY = "policy";

    private static ExchangeSyncFactory instance;

    public static ExchangeSyncFactory getInstance() {
        if (instance == null) {
            String className = LC.data_source_xsync_factory_class.value();
            if (className != null && !className.equals("")) {
                try {
                    instance = (ExchangeSyncFactory)Class.forName(className).newInstance();
                } catch (Exception e) {
                    ZimbraLog.xsync.error(
                        "could not instantiate ExchangeFactory interface of class '"
                            + className + "'; defaulting to ZimbraServices", e);
                }
            }
            if (instance == null)
                instance = new ExchangeSyncFactory();
        }
        return instance;
    }

    private String policyKey = null;

    ExchangeFolderSync newFolderSync(DataSource ds) throws ServiceException {
        return new ExchangeFolderSync(ds);
    }

    String getUserAgent() {
        return "Zimbra";
    }

    public String getPolicyKey(Mailbox mbox) throws ServiceException {
        if (policyKey == null) {
            Metadata metadata = mbox.getConfig(getContext(mbox, false), XSYNC_SECTION);
            if (metadata != null)
                policyKey = metadata.get(XSYNC_POLICYKEY, "0");
            else
                policyKey = "0";
        }
        return policyKey;
    }

    public void setPolicyKey(Mailbox mbox, String policyKey) throws ServiceException {
        Metadata metadata = new Metadata();
        metadata.put(XSYNC_POLICYKEY, policyKey);
        mbox.setConfig(getContext(mbox, false), XSYNC_SECTION, metadata);
        this.policyKey = policyKey;
    }

    public SyncSettings getSyncSettings(DataSource ds) throws ServiceException {
        String deviceId = ZimbraApplication.getInstance().getClientId();
        deviceId = StringUtil.join("", deviceId.split("-"));

        String host = ds.getHost();
        int port = ds.getPort();
        boolean useSSL = ds.isSslEnabled();
        String domain = ds.getDomain();
        String username = ds.getUsername();
        String password = ds.getDecryptedPassword();

        return new SyncSettings(getUserAgent(), deviceId, host, port, useSSL, domain, username, password);
    }

    protected OperationContext getContext(Mailbox mbox, boolean markChanges) throws ServiceException {
        return new OperationContext(mbox);
    }

    protected ChangeTracker getClientChanges(DataSource ds, Map<Integer, ExchangeFolderMapping> folderMappingsByClientId) throws ServiceException {
        return new ChangeTracker(ds, folderMappingsByClientId);
    }
}
