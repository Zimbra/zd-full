/*
 * 
 */
package com.zimbra.zimbrasync.client;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.zimbrasync.client.cmd.DocumentSync.DocumentAppData;
import com.zimbra.zimbrasync.client.cmd.DocumentSync.DocumentSyncServerCommandExecutor;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientAdd;
import com.zimbra.zimbrasync.client.cmd.Sync.AirSyncClientChange;

public class MailboxDocumentSync extends MailboxCollectionSync implements DocumentSyncServerCommandExecutor {

    public MailboxDocumentSync(Folder folder, String collectionId, ChangeTracker tracker) {
        super(folder, collectionId, tracker);
    }
    
    @Override
    protected byte getItemType() {
        return MailItem.TYPE_WIKI;
    }

    @Override
    public AirSyncClientAdd newClientAdd(int id) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AirSyncClientChange newClientChange(int id) throws ServiceException {
        // TODO Auto-generated method stub
        return null;
    }

    public void doServerAdd(String serverId, DocumentAppData appData) {
        // TODO Auto-generated method stub
        
    }
    
    public void doServerChange(String serverId, DocumentAppData appData) {
        // TODO Auto-generated method stub
        
    }
}
