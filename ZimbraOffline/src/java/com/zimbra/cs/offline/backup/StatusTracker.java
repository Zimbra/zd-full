/*
 * 
 */
package com.zimbra.cs.offline.backup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.offline.OfflineLog;

public class StatusTracker {
    ConcurrentMap<String, Process> inProgress = new ConcurrentHashMap<String, Process>();
    
    public static enum Process {BACKUP, RESTORE};
    
    synchronized boolean markAccountInProgress(String accountId, Process p) throws ServiceException {
        if (isAccountInProgress(accountId, p)) {
            return false;
        }
        inProgress.put(accountId, p);
        return true;
    }
    
    void markAccountDone(String accountId) {
        inProgress.remove(accountId);
    }
    
    boolean isAccountInProgress(String accountId, Process p) throws ServiceException {
        Process current = inProgress.get(accountId);
        if (current != null) {
            OfflineLog.offline.warn("Account already in "+current+" process");
            return true;
        } else {
            return false;
        }
    }
}
