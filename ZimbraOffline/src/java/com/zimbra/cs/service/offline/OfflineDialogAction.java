/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.GalSync;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailboxManager;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.HeapDumpScanner;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * <DialogActionRequest id="accountId" type="dialog-type" action="yes|no" />
 */
public class OfflineDialogAction extends DocumentHandler {
    public static final String DIALOG_TYPE_RESYNC = "resync";
    public static final String DIALOG_RESYNC_MSG = "Client hasn't been synced for too long. Needs to reset mailbox and resync.";
    public static final String DIALOG_TYPE_FOLDER_MOVE_START = "foldermove_start";
    public static final String DIALOG_TYPE_FOLDER_MOVE_START_MSG = "Folder move started";
    public static final String DIALOG_TYPE_FOLDER_MOVE_COMPLETE = "foldermove_complete";
    public static final String DIALOG_TYPE_FOLDER_MOVE_COMPLETE_MSG = "Folder move completed";
    public static final String DIALOG_TYPE_FOLDER_MOVE_FAIL = "foldermove_failed";
    public static final String DIALOG_TYPE_FOLDER_MOVE_FAIL_MSG = "Folder move failed";
    public static final String DIALOG_TYPE_HEAP_DUMP_UPLOAD_CONSENT = "heapdump_upload";
    public static final String DIALOG_HEAP_DUMP_UPLOAD_CONSENT_MSG = "Do we have your consent to upload heap dump of ZD's previous crash ?";
    public static final String DIALOG_TYPE_GAL_RESYNC = "gal_resync";
    public static final String DIALOG_TYPE_GAL_RESYNC_MSG = "GAL needs resync, please click Reset-GAL button at account setup page";

    private static enum DialogType {
        resync, heapdump_upload, gal_resync
    }

    private static enum DialogAction {
        yes, no
    }

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        String accountId = request.getAttribute(AdminConstants.E_ID);
        DialogType type = null;
        DialogAction action = null;
        try {
            type = DialogType.valueOf(request.getAttribute(AdminConstants.A_TYPE));
            action = DialogAction.valueOf(request.getAttribute(AdminConstants.A_ACTION));
        } catch (IllegalArgumentException e) {
            OfflineLog.offline.warn("dialog [type,action] cannot be recognized. [%s , %s]",
                    request.getAttribute(AdminConstants.A_TYPE), request.getAttribute(AdminConstants.A_ACTION));
            throw ServiceException.INVALID_REQUEST("dialogue type/action cannot be recognized", e);
        }

        handleDialog(accountId, type, action);
        OfflineSyncManager.getInstance().unregisterDialog(accountId, type.name());

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element resp = zsc.createElement(OfflineConstants.DIALOG_ACTION_RESPONSE);
        return resp;
    }

    protected void handleDialog(String accountId, DialogType type, DialogAction action) throws ServiceException {
        switch (type) {
        case resync:
            handleResync(accountId, action);
            break;
        case heapdump_upload:
            handleHeapdumpUpload(accountId, action);
            break;
        case gal_resync:
            handleGalResync(accountId, action);
            break;
        }
    }

    private void handleResync(String accountId, DialogAction action) throws ServiceException {
        switch (action) {
        case yes:
            OfflineLog.offline.debug("about to resync mailbox %s", accountId);
            Mailbox mbox = MailboxManager.getInstance().getMailboxByAccountId(accountId);
            mbox.deleteMailbox();
            break;
        case no:
            OfflineLog.offline.debug("user refused to resync mailbox %s", accountId);
            break;
        }
    }

    private void handleHeapdumpUpload(String accountId, DialogAction action) {
        switch (action) {
        case yes:
            OfflineLog.offline.info("user chose to upload heap dump.");
            HeapDumpScanner.getInstance().upload();
            break;
        case no:
            OfflineLog.offline.info("user chose NOT to upload heap dump.");
            break;
        }
    }

    private void handleGalResync(String accountId, DialogAction action) throws ServiceException {
        switch (action) {
        case yes:
            OfflineLog.offline.info("user chose to resync GAL.");
            OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
            OfflineAccount account = (OfflineAccount) prov.get(AccountBy.id, accountId);
            if (account.isFeatureGalEnabled() && account.isFeatureGalSyncEnabled()) {
                OfflineAccount galAccount = (OfflineAccount) prov.getGalAccountByAccount(account);
                if (galAccount != null) {
                    GalSync.getInstance().resetGal(galAccount);        
                }
            }
            break;
        case no:
            OfflineLog.offline.info("user chose NOT to resync GAL.");
            break;
        }
    }
}
