/*
 * 
 */
package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.db.DbMailbox;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.redolog.op.RedoableOp;

public abstract class DesktopMailbox extends Mailbox {

    static DesktopMailbox newMailbox(MailboxData data) throws ServiceException {
        OfflineAccount account = (OfflineAccount)Provisioning.getInstance()
            .get(AccountBy.id, data.accountId);
        if (account == null)
            throw AccountServiceException.NO_SUCH_ACCOUNT(data.accountId);

        if (account.isLocalAccount())
            return new LocalMailbox(data);

        if (account.isZcsAccount())
            return new ZcsMailbox(data);

        if (account.isExchangeAccount())
            return new ExchangeMailbox(data);

        return new DataSourceMailbox(data);
    }

    public static final String FAILURE_PATH = "Error Reports";
    public static final String NOTIFICATIONS_PATH = "Notification Mountpoints";
    public static final String OUTBOX_PATH = "Outbox";

    public static final int ID_FOLDER_NOTIFICATIONS = 250;
    public static final int ID_FOLDER_FAILURE = 252;
    public static final int ID_FOLDER_OUTBOX = 254;

    private static final String CONFIG_OFFLINE_VERSION = "offline_ver";

    private OfflineMailboxVersion offlineVersion;
    private boolean isOfflineVerCheckComplete;

    public DesktopMailbox(MailboxData data) throws ServiceException {
        super(data);
    }

    public OfflineAccount getOfflineAccount() throws ServiceException {
        return (OfflineAccount)getAccount();
    }

    @Override
    protected synchronized void initialize() throws ServiceException {
        super.initialize();
        // set the version to CURRENT
        Metadata md = new Metadata();
        offlineVersion = OfflineMailboxVersion.CURRENT();
        offlineVersion.writeToMetadata(md);
        DbMailbox.updateConfig(this, CONFIG_OFFLINE_VERSION, md);
    }

    @Override
    boolean open() throws ServiceException {
        if (super.open()) {
            ensureSystemFolderExists();
            checkOfflineVersion();
            return true;
        }
        return false;
    }

    synchronized void checkOfflineVersion() throws ServiceException {
        if (!isOfflineVerCheckComplete) {
            if (offlineVersion == null) {
                Metadata md = getConfig(null, CONFIG_OFFLINE_VERSION);
                offlineVersion = OfflineMailboxVersion.fromMetadata(md);
            }
            if(!offlineVersion.atLeast(5)) {
                if(!offlineVersion.atLeast(3)) {
                    if (!offlineVersion.atLeast(2)) {
                        OfflineMailboxMigration.doMigrationV2(this);
                    }
                    OfflineMailboxMigration.doMigrationV3(this);
                }
                OfflineMailboxMigration.doMigrationV5(this);
                updateOfflineVersion(OfflineMailboxVersion.CURRENT());
            }
            isOfflineVerCheckComplete = true;
        }
    }

    private synchronized void updateOfflineVersion(OfflineMailboxVersion ver)
        throws ServiceException {
        offlineVersion = ver;
        Metadata md = getConfig(null, CONFIG_OFFLINE_VERSION);
        if (md == null)
            md = new Metadata();
        offlineVersion.writeToMetadata(md);
        setConfig(null, CONFIG_OFFLINE_VERSION, md);
    }

    synchronized void ensureSystemFolderExists() throws ServiceException {
        try {
            getFolderById(ID_FOLDER_FAILURE);
        } catch (MailServiceException.NoSuchItemException x) {
            CreateFolder redo = new CreateFolder(getId(), FAILURE_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR_RGB, null);

            redo.setFolderId(ID_FOLDER_FAILURE);
            redo.start(System.currentTimeMillis());
            createFolder(new TracelessContext(redo), FAILURE_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR_RGB, null);
        }
    }

    @Override
    public boolean dumpsterEnabled() {
        return false;
    }
    
    @Override
    protected void migrateWikiFolders() throws ServiceException {
        OfflineLog.offline.debug("wiki folder migration skipped");
    }

    @Override
    boolean isChildFolderPermitted(int folderId) {
        return super.isChildFolderPermitted(folderId) && folderId != ID_FOLDER_OUTBOX && folderId != ID_FOLDER_NOTIFICATIONS && folderId != ID_FOLDER_FAILURE; 
    }

    @Override
    protected boolean needRedo(OperationContext octxt,  RedoableOp recorder) {
        if (OfflineLC.zdesktop_redolog_enabled.booleanValue()) {
            return super.needRedo(octxt, recorder);
        } else {
            return false;
        }
    }
}
