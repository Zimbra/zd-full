/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

public abstract class FolderCommand extends SyncCommand {
    
    public FolderCommand(String clientSyncKey) {
        super(clientSyncKey);
    }
    
    @Override
    protected void handleStatusError() throws ResponseStatusException {
        switch (status) {
        case 2:
            throw ResponseStatusException.FolderExists();
        case 3:
            throw ResponseStatusException.FolderImmutable();
        case 4:
            throw ResponseStatusException.FolderNotFound();
        case 5:
            throw ResponseStatusException.ParentNotFound();
        case 6:
            throw ResponseStatusException.ServerError();
        case 7:
            throw ResponseStatusException.AccessDenied();
        case 8:
            throw ResponseStatusException.RequestTimedOut();
        case 9:
            throw ResponseStatusException.InvalidSyncKey();
        case 10:
            throw ResponseStatusException.MalformedRequest();
        case 11:
        default:
            throw ResponseStatusException.UnknownError();
        }
    }
}
