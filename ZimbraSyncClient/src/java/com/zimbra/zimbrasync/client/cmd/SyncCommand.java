/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

public abstract class SyncCommand extends Command {

    protected String clientSyncKey;
    protected String serverSyncKey;

    public SyncCommand(String clientSyncKey) {
        this.clientSyncKey = clientSyncKey;
    }
    
    public String getClientSyncKey() {
        return clientSyncKey;
    }

    public String getServerSyncKey() {
        assert serverSyncKey != null;
        return serverSyncKey;
    }
}
