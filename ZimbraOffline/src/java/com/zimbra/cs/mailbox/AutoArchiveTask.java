/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.util.ZimbraApplication;

public class AutoArchiveTask extends TimerTask {

    public AutoArchiveTask() {
    }

    @Override
    public void run() {
        if (ZimbraApplication.getInstance().isShutdown()) {
            this.cancel();
            return;
        }
        Account acct = null;
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        try {
            acct = AutoArchive.getAccountForAutoArchiving();
            if (acct == null) {
                return;
            }

            OfflineLog.offline.info("Auto Archiving for account %s has begun.", acct.getName());

            Mailbox mbox = MailboxManager.getInstance().getMailboxByAccount(acct);
            Folder rootFolder = mbox.getFolderById(Mailbox.ID_FOLDER_USER_ROOT);
            List<Folder> folders = rootFolder.getSubfolderHierarchy();

            Account localAccount = prov.getLocalAccount();
            LocalMailbox localMBox = (LocalMailbox) MailboxManager.getInstance().getMailboxByAccount(localAccount);

            long cutoffTime = getArchivingCutoffTime();

            for (Folder folder : folders) {
                List<Integer> list = null;
                //Filter out emails for auto archiving process
                if (folder.getType() == MailItem.TYPE_FOLDER && !(folder.getId() == Mailbox.ID_FOLDER_DRAFTS)) {
                    list = mbox.getItemsByDate(mbox.getOperationContext(), folder, cutoffTime);
                    //Auto archive only if there are items
                    if (list.size() > 0) {
                        OfflineLog.offline.debug("%d emails from %s folder are filtered for auto archiving", list.size(), folder.getName());
                        doAutoArchive(mbox, localMBox, folder, list);
                    }
                }
            }
            OfflineLog.offline.info("Auto archiving completed at %d", System.currentTimeMillis());
            AutoArchive.persistLastArchiveInfo();
        } catch(ServiceException se) {
            handleAutoArchiveFailure(se);
        } catch(Exception e) {
            handleAutoArchiveFailure(e);
        }
    }

    /**
     * Copies filtered emails from primary mailbox to local mailbox and deletes those emails from mailbox server.
     * @param mbox
     * @param localMBox
     * @param localFolder
     * @param items
     * @return Returns number of emails archived successfully.
     * @throws ServiceException
     */
    private int copyToLocalAndDeleteFromMailbox(Mailbox mbox, LocalMailbox localMBox, Folder localFolder, List<Integer> items) throws ServiceException {
        int autoArchivedEmailCount = 0;
        for (int itemID : items) {
            boolean isCopiedToLocalFolder = false;
            //Add message to local folder
            MailItem item = mbox.getItemById(mbox.getOperationContext(), itemID, MailItem.TYPE_MESSAGE);

            DeliveryOptions opt = new DeliveryOptions().
                    setFolderId(localFolder.getId()).setNoICal(true).
                    setFlags(item.getFlagBitmask());
            try {
                //Check if email has tag
                List<Tag> tags = item.getTagList();
                if (null != tags && tags.size() > 0) {
                    String tagString = getTagStringForArchivedItem(localMBox, tags, item);
                    opt = opt.setTagString(tagString);
                }
                //Add message to local mailbox
                localMBox.addMessage(localMBox.getOperationContext(), item.getContentStream(), (int)item.getSize(), item.getDate(), opt);
                isCopiedToLocalFolder = true;
                OfflineLog.offline.debug("Mail item %d is archived", itemID);
            } catch (IOException e) {
                OfflineLog.offline.error("Archiving of email item %d is failed.", itemID, e);
            } catch (ServiceException e) {
                OfflineLog.offline.error("Archiving of email item %d is failed.", itemID, e);
            }
            //Delete an email from mailbox only if copy operation is successful.
            if (isCopiedToLocalFolder) {
                mbox.delete(mbox.getOperationContext(), itemID, MailItem.TYPE_MESSAGE);
                OfflineLog.offline.debug("Mail item %d is deleted from mailbox.", itemID);
                autoArchivedEmailCount++;
            }
        }
        return autoArchivedEmailCount;
    }

    /**
     * Gets tag information from email Item. Checks whether same tag exist in Local mailbox, if not then new tag under local mailbox is created.
     * @param localMBox
     * @param item
     * @param tags of email from primary mailbox
     * @return Tag string with tag id's correspond to local mailbox.
     * @throws ServiceException
     */
     static String getTagStringForArchivedItem(Mailbox localMBox, List<Tag> tags, MailItem item) throws ServiceException {
        Integer [] localTags = new Integer[tags.size()];
        int cnt = 0;
        for (Tag tag : tags) {
            Tag localTag = null;
            try {
                //Checking if same tag is present in Local mailbox.
                localTag = localMBox.getTagByName(tag.getName());
            } catch (MailServiceException.NoSuchItemException e) {
                //Create new tag under Local mailbox.
                localTag = localMBox.createTag(localMBox.getOperationContext(), tag.getName(), tag.getColor());
                OfflineLog.offline.info
                ("New tag %s is created under Local Maibox.", tag.getName());
            }
            localTags[cnt++] = localTag.getId();
        }
        return StringUtil.join(",", localTags);
    }

    /**
     * Calculates cutoff time based on number of days specified by user.
     */
    private long getArchivingCutoffTime() throws ServiceException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DATE, -AutoArchive.getAgeInDays());
        return cal.getTimeInMillis();
    }

    /**
     * Check if local folder exist if not then create new local folder.
     * @param localMBox
     * @param folder
     * @return
     * @throws ServiceException
     */
    private Folder getLocalFolderByName(LocalMailbox localMBox, Folder folder) throws ServiceException {
        Folder localFolder = null;
        OperationContext localOctxt = localMBox.getOperationContext();
        OfflineLog.offline.debug("Archiving emails from folder %s." , folder.getPath());
        try {
            localFolder = localMBox.getFolderByPath(localOctxt, folder.getPath());
        } catch(MailServiceException.NoSuchItemException e) {
            //Local folder doesn't exist. Create it.
            localFolder = localMBox.createFolder(localOctxt, folder.getPath(), (byte) 0, MailItem.TYPE_MESSAGE, folder.getFlagBitmask(), folder.getColor(), folder.getUrl());
            OfflineLog.offline.debug("Local folder (%s) is created to store archived emails.", localFolder.getPath());
        }
        localMBox.setColor(localOctxt, localFolder.getId(), MailItem.TYPE_FOLDER, folder.getColor());
        return localFolder;
    }

    /**
     * Archive filtered emails from mailbox to local folders.
     * @param mbox
     * @param localMBox
     * @param folder
     * @param list
     * @throws ServiceException
     */
    private void doAutoArchive(Mailbox mbox, LocalMailbox localMBox, Folder folder, List<Integer> list) throws ServiceException {
        OfflineLog.offline.debug("Archiving emails from folder %s." , folder.getPath());

        Folder localFolder = getLocalFolderByName(localMBox, folder);

        int autoArchivedEmailCount = 0;

        //check if batch processing is needed
        int batchSize = (int) OfflineLC.zdesktop_auto_archive_batch_size.longValue();

        if (list.size() > batchSize) {
            List<Integer> items = null;

            for (int index = 0 ; index < list.size() ; index += batchSize) {
                items = list.subList(index, Math.min(index + batchSize, list.size()));
                autoArchivedEmailCount += copyToLocalAndDeleteFromMailbox(mbox, localMBox, localFolder, items);
                try {
                    Thread.sleep(OfflineLC.zdesktop_auto_archive_sleep_time.longValue());
                } catch (InterruptedException e) {
                    OfflineLog.offline.error("InterruptedException occurred during auto archive", e);
                }
            }
        } else {
            autoArchivedEmailCount = copyToLocalAndDeleteFromMailbox(mbox, localMBox, localFolder, list);
        }
        OfflineLog.offline.debug("Total %d emails are archived successfully from %s folder", autoArchivedEmailCount, folder.getName());
    }

    /**
     * Cancels currently running auto archive timer and reschedules auto archiving
     * @param e
     * @throws ServiceException
     */
    private void handleAutoArchiveFailure(Throwable e) {
        OfflineLog.offline.error("Auto archiving is failed. Exception occurred in auto archiving", e);
        cancel();
        try {
            AutoArchiveTimer.lastAutoArchiveFailed();
        } catch (ServiceException e1) {
            OfflineLog.offline.error("Auto archive failed", e1);
        }
    }
}
