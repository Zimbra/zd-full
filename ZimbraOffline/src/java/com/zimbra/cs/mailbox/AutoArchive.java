/*
 * 
 */

package com.zimbra.cs.mailbox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.common.OfflineConstants.AutoArchiveFrequency;

public class AutoArchive {
    /**
     * @return Auto archiving is enabled or not. Get this info from DB.
     * @throws ServiceException
     */
    public static boolean isAutoArchivingEnabled() throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        return localAccount.getBooleanAttr(OfflineProvisioning.A_offlineAutoArchiveEnabled, false);
    }

    /**
     * @return Get number of days configured from DB.
     * @throws ServiceException
     */
    public static int getAgeInDays() throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        return Integer.parseInt(localAccount.getAttr(OfflineProvisioning.A_offlineAutoArchiveNoOfDays));
    }

    /**
     * @return Get frequency set for auto-archiving from DB.
     * @throws ServiceException
     */
    public static AutoArchiveFrequency getFrequency() throws ServiceException {
        Account localAccount = OfflineProvisioning.getOfflineInstance().getLocalAccount();
        return AutoArchiveFrequency.getAutoArchiveFrequency(localAccount.getAttr(OfflineProvisioning.A_offlineAutoArchiveFrequency));
    }
    /**
     * Get account on which auto archiving will run.
     * @return
     */
    public static Account getAccountForAutoArchiving()  {
        List<Account> dsAccounts = null;
        try {
            dsAccounts = OfflineProvisioning.getOfflineInstance().getAllZcsAccounts();
        } catch (ServiceException e) {
            OfflineLog.offline.error("Exception occurred while retirving zcs accounts", e);
        }
        Account acct = null;
        if (dsAccounts.size() == 0) {
            return null;
        }
        //Auto archiving task runs only for single account.
        if (dsAccounts.size() == 1) {
            acct = dsAccounts.get(0);
        } else {
            //TODO: If there are multiple ZCS accounts then account will be specified in preferences.
        }
        return acct;
    }

    /**
     * Persist last archive information.
     * @param acct
     */
    public static void persistLastArchiveInfo() {
        try {
            Map<String, Object> attrs = new HashMap<String, Object>(1);
            attrs.put(OfflineConstants.A_offlineLastAutoArchive, Long.toString(System.currentTimeMillis()));
            OfflineProvisioning.getOfflineInstance().modifyAttrs(OfflineProvisioning.getOfflineInstance().getLocalAccount(), attrs);
        } catch (ServiceException e) {
            OfflineLog.offline.error("Exception occurred while persising auto archive information in database", e);
        }
    }

    /**
     * getLastAutoArchiveTimestamp
     * @return
     */
    public static String getLastAutoArchiveTimestamp() {
        try {
            return OfflineProvisioning.getOfflineInstance().
                    getLocalAccount().getAttr(OfflineProvisioning.A_offlineLastAutoArchive, "0");
        } catch(ServiceException e) { 
            OfflineLog.offline.error("Exception occured while retrieving timestamp of last archiving", e); 
        }
        return "0";
    }
}
