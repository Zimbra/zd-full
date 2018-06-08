/*
 * 
 */
package com.zimbra.zimbrasync.client;

import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.zimbrasync.client.cmd.TasksSync.TasksSyncServerCommandExecutor;

public class MailboxTasksSync extends MailboxCalendarSync implements TasksSyncServerCommandExecutor {

    public MailboxTasksSync(Folder folder, String collectionId, ChangeTracker tracker) {
        super(folder, collectionId, tracker);
    }
    
    @Override
    protected byte getItemType() {
        return MailItem.TYPE_TASK;
    }
}
