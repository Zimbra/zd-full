/*
 * 
 */
package com.zimbra.zimbrasync.client;

import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.zimbrasync.client.cmd.CalendarSync;
import com.zimbra.zimbrasync.client.cmd.ContactsSync;
import com.zimbra.zimbrasync.client.cmd.DocumentSync;
import com.zimbra.zimbrasync.client.cmd.EmailSync;
import com.zimbra.zimbrasync.client.cmd.Sync;
import com.zimbra.zimbrasync.client.cmd.TasksSync;

public class CollectionSyncFactory {
    
    private static CollectionSyncFactory instance = new CollectionSyncFactory();
    
    public static CollectionSyncFactory instance() {
        return instance;
    }

    private CollectionSyncFactory() {}
    
    public Sync createSyncCommand(Folder folder, String collectionId, String syncKey, ChangeTracker tracker) {
        switch (folder.getDefaultView()) {
        case MailItem.TYPE_APPOINTMENT:
            return new CalendarSync(collectionId, syncKey, new MailboxCalendarSync(folder, collectionId, tracker));
        case MailItem.TYPE_CONTACT:
            return new ContactsSync(collectionId, syncKey, new MailboxContactsSync(folder, collectionId, tracker));
        case MailItem.TYPE_TASK:
            return new TasksSync(collectionId, syncKey, new MailboxTasksSync(folder, collectionId, tracker));
        case MailItem.TYPE_WIKI:
            return new DocumentSync(collectionId, syncKey, new MailboxDocumentSync(folder, collectionId, tracker));
        case MailItem.TYPE_MESSAGE:
        case MailItem.TYPE_CHAT:
        case MailItem.TYPE_UNKNOWN:
            return new EmailSync(collectionId, syncKey, new MailboxEmailSync(folder, collectionId, tracker));
        default:
            //log
            throw new RuntimeException("unexpected type " + folder.getDefaultView());
        }
    }
    
    public MailboxCollectionSync createCollectionSync(Folder folder, String collectionId, ChangeTracker tracker) {
        switch (folder.getDefaultView()) {
        case MailItem.TYPE_APPOINTMENT:
            return new MailboxCalendarSync(folder, collectionId, tracker);
        case MailItem.TYPE_CONTACT:
            return new MailboxContactsSync(folder, collectionId, tracker);
        case MailItem.TYPE_TASK:
            return new MailboxTasksSync(folder, collectionId, tracker);
        case MailItem.TYPE_WIKI:
            return new MailboxDocumentSync(folder, collectionId, tracker);
        case MailItem.TYPE_MESSAGE:
        case MailItem.TYPE_CHAT:
        case MailItem.TYPE_UNKNOWN:
            return new MailboxEmailSync(folder, collectionId, tracker);
        default:
            throw new RuntimeException("unexpected folder type " + folder.getDefaultView());
        }
    }
}
