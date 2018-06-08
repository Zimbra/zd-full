/*
 * 
 */
package com.zimbra.cs.offline.backup;

import java.util.List;

public class AccountBackupInfo {
    private List<BackupInfo> backups;
    private String accountId;
    public List<BackupInfo> getBackups() {
        return backups;
    }
    public void setBackups(List<BackupInfo> backups) {
        this.backups = backups;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
