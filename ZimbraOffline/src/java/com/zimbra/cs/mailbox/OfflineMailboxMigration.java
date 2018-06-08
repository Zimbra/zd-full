/*
 * 
 */
package com.zimbra.cs.mailbox;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.DataSource.ConnectionType;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.imap.ImapFolder;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbImapFolder;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.index.SortBy;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.common.OfflineConstants;

class OfflineMailboxMigration {
    /*
     * In public beta5 build 1418 we had a bug 33763 that pushed caldav created calendar folder to IMAP server.
     * If then the mailbox is reset or is synced to a new install, IMAP code will sync down that folder as a mailbox,
     * causing caldav code to skip sync of any calendar items.
     *
     * Solution:
     * 1. If we see the folder is still a calendar folder, find the IMAP_FOLDER mapping of this folder and change mapped
     *    local ID to a bogus number, which will cause imap code to delete the remote corresponding folder.
     *
     * 2. If we see the folder is a mail folder, do same as 1, but in addition also change its view from message to
     *    appointment.
     */
    static void doMigrationV2(DesktopMailbox mbox) throws ServiceException {
        if (!(mbox instanceof DataSourceMailbox)) {
            return;
        }

        OfflineAccount account = mbox.getOfflineAccount();
        if (!account.isDataSourceAccount()) {
            return;
        }

        OfflineDataSource ds = (OfflineDataSource)OfflineProvisioning.getOfflineInstance().getDataSource(account);
        if (!ds.isYahoo() && !ds.isGmail()) {
            return;
        }

        Collection<DataSourceItem> dsItems = DbDataSource.getAllMappings(ds);
        List<MailItem> folders = mbox.getItemList(null, MailItem.TYPE_FOLDER, Mailbox.ID_FOLDER_USER_ROOT);
        Set<Integer> folderIds = new HashSet<Integer>(folders.size());
        for (MailItem mi: folders)
            folderIds.add(mi.getId());
        for (DataSourceItem dsi : dsItems)
            if (folderIds.contains(dsi.itemId) && dsi.remoteId != null &&
                (dsi.remoteId.toLowerCase().startsWith("/dav/") || dsi.remoteId.toLowerCase().startsWith("/calendar/dav/")))
                fixFolder(mbox, ds, dsi.itemId);
    }

    private static void fixFolder(Mailbox mbox, DataSource ds, int folderId) throws ServiceException {
        Folder folder = mbox.getFolderById(folderId);
        if (folder.getDefaultView() != MailItem.TYPE_APPOINTMENT)
            mbox.setFolderDefaultView(null, folderId, MailItem.TYPE_APPOINTMENT);

        if ((folder.getFlagBitmask() & Flag.BITMASK_CHECKED) == 0)
            mbox.alterTag(null, folderId, MailItem.TYPE_FOLDER, Flag.ID_FLAG_CHECKED, true);

        if ((folder.getFlagBitmask() & Flag.BITMASK_SYNC) != 0)
            mbox.alterTag(null, folderId, MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNC, false);

        if ((folder.getFlagBitmask() & Flag.BITMASK_SYNCFOLDER) != 0)
            mbox.alterTag(null, folderId, MailItem.TYPE_FOLDER, Flag.ID_FLAG_SYNCFOLDER, false);

        ImapFolder imapFolder = DbImapFolder.getImapFolders(mbox, ds).getByItemId(folderId);
        if (imapFolder != null) {
            DbImapFolder.deleteImapFolder(mbox, ds, imapFolder);
            DbImapFolder.createImapFolder(mbox, ds, -imapFolder.getItemId(), imapFolder.getLocalPath(), imapFolder.getRemoteId(), imapFolder.getUidValidity());
        }
    }

    static void doMigrationV3(DesktopMailbox mbox) throws ServiceException {
        if (mbox instanceof DataSourceMailbox) {
            OfflineAccount account = mbox.getOfflineAccount();
            if (account.isDataSourceAccount()) {
                OfflineDataSource ds = (OfflineDataSource)
                    OfflineProvisioning.getOfflineInstance().getDataSource(account);
                checkMappings(ds, ds.getContactSyncDataSource(), MailItem.TYPE_CONTACT, "contact");
                checkMappings(ds, ds.getCalendarSyncDataSource(), MailItem.TYPE_APPOINTMENT, "appointment");
            }
        }
    }
    /*
     * Yahoo! stopped support for their mail API's which ZD uses for various email operations. 
     * Now ZD uses standard IMAP and SMTP protocol for email operations.
     * These changes are required to update IMAP and SMTP setting for existing Yahoo! accounts.
     * Refer https://bugzilla.zimbra.com/show_bug.cgi?id=99315
     */
    static void doMigrationV5(DesktopMailbox mbox) throws ServiceException {
        if (!(mbox instanceof DataSourceMailbox))
            return;

        OfflineAccount account = mbox.getOfflineAccount();
        if (!account.isDataSourceAccount())
            return;

        OfflineDataSource ds = (OfflineDataSource) OfflineProvisioning.getOfflineInstance().getDataSource(account);

        if (ds.isYahoo()) {
            ZimbraLog.datasource.debug("doMigrationV4 : Existing Yahoo account is found. Updating SMTP and IMAP settings in database.");
            String email = ds.getEmailAddress();
            String imapHost = email.endsWith("@yahoo.co.jp") ? OfflineLC.zdesktop_yahoo_imap_host_jp.value() : OfflineLC.zdesktop_yahoo_imap_host.value();
            String imapPort = OfflineLC.zdesktop_yahoo_imap_ssl_port.value();

            String smtpHost = email.endsWith("@yahoo.co.jp") ? OfflineLC.zdesktop_yahoo_smtp_host_jp.value() : OfflineLC.zdesktop_yahoo_smtp_host.value();

            Map<String, Object> dsAttrs = new HashMap<String, Object>();
            dsAttrs.put(Provisioning.A_zimbraDataSourceHost, imapHost);
            dsAttrs.put(Provisioning.A_zimbraDataSourcePort, imapPort);
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpEnabled, Provisioning.TRUE);
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpHost, smtpHost);
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpPort, OfflineLC.zdesktop_yahoo_smtp_ssl_port.value());
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpConnectionType, ConnectionType.ssl.toString());
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthRequired, Provisioning.TRUE);

            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthUsername, email);
            dsAttrs.put(OfflineConstants.A_zimbraDataSourceSmtpAuthPassword, ds.getDecryptedPassword());

            OfflineProvisioning.getInstance().modifyDataSource(account, ds.getId(), dsAttrs);
        }
    }

    private static void checkMappings(OfflineDataSource fromDs,
                                      OfflineDataSource toDs,
                                      byte type, String typeName) throws ServiceException {
        if (toDs == null) return;
        Mailbox mbox = fromDs.getMailbox();
        ZimbraLog.datasource.info("Migrating offline mailbox %s db mappings", typeName);
        for (MailItem item : mbox.getItemList(null, type)) {
            // Move contact mappings to new contact data source
            DataSourceItem dsi = DbDataSource.getMapping(fromDs, item.getId());
            if (dsi.remoteId != null) {
                ZimbraLog.datasource.debug(
                    "Moving db mapping for %s item id %d from data source '%s' to '%s'",
                    typeName, item.getId(), fromDs.getName(), toDs.getName());
                DbDataSource.deleteMapping(fromDs, item.getId());
                DbDataSource.addMapping(toDs, dsi);
            }
        }
        for (Folder folder : mbox.getFolderList(null, SortBy.NONE)) {
            if (folder.getDefaultView() == type) {
                DataSourceItem dsi = DbDataSource.getMapping(fromDs, folder.getId());
                if (dsi.remoteId != null) {
                    ZimbraLog.datasource.debug(
                        "Moving db mapping for %s folder '%s' from data source '%s' to '%s'",
                        typeName, folder.getName(), fromDs.getName(), toDs.getName());
                    DbDataSource.deleteMapping(fromDs, folder.getId());
                    DbDataSource.addMapping(toDs, dsi);
                }
            }
        }
        ZimbraLog.datasource.info("Done migrating offline mailbox %s db mappings", typeName);
    }
}
