/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.io.InputStream;

import com.zimbra.common.util.ByteUtil;
import com.zimbra.zimbrasync.client.cmd.CalendarSync.CalendarSyncServerCommandExecutor;
import com.zimbra.zimbrasync.client.cmd.ContactsSync.ContactsSyncServerCommandExecutor;
import com.zimbra.zimbrasync.client.cmd.EmailSync.EmailAppData;
import com.zimbra.zimbrasync.client.cmd.EmailSync.EmailSyncServerCommandExcecutor;
import com.zimbra.zimbrasync.client.cmd.TasksSync.TasksSyncServerCommandExecutor;
import com.zimbra.zimbrasync.data.CalendarAppData;
import com.zimbra.zimbrasync.data.ContactAppData;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

/**
 * 
 * @author smukhopadhyay
 *
 */
public class CollectionSync extends Command {
    
    static final String COLLECTION_CLASS_EMAIL = "Email";
    static final String COLLECTION_CLASS_CALENDAR = "Calendar";
    static final String COLLECTION_CLASS_CONTACTS = "Contacts";
    static final String COLLECTION_CLASS_TASKS = "Tasks";
    

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }
    
    // <?xml version="1.0" encoding="utf-8"?>
    // <Sync xmlns="AirSync">
    //    <Status>4</Status>
    // </Sync>
    
    // <?xml version="1.0" encoding="utf-8"?>
    // <Sync xmlns="AirSync">
    //     <Collections>
    //         <Collection>
    //             <Class>Contacts</Class>
    //             <SyncKey>1</SyncKey>
    //             <CollectionId>2</CollectionId>
    //             <Status>1</Status>
    //         </Collection>
    //     </Collections>
    // </Sync>
    
    // <?xml version="1.0" encoding="utf-8"?>
    // <Sync>
    //    <Collections>
    //        <Collection>
    //            <Class>Email</Class>
    //            <CollectionId>2</CollectionId>
    //            <Status>3</Status>
    //        </Collection>
    //    </Collections>
    // </Sync>
    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        while (bp.next() == START_TAG && NAMESPACE_AIRSYNC.equals(bp.getNamespace())) {
            if (AIRSYNC_COLLECTIONS.equals(bp.getName())) {
                while (bp.next() == START_TAG) { //Collection
                    bp.require(START_TAG, NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
                    String cClass = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_CLASS);
                    Sync sync = null;
                    if (cClass.equals(COLLECTION_CLASS_EMAIL)) {
                        EmailSyncExecutor executor = new EmailSyncExecutor();
                        sync = new EmailSync(null, null, executor);
                    } else if (cClass.equals(COLLECTION_CLASS_CALENDAR)) {
                        CalendarSyncExecutor executor = new CalendarSyncExecutor();
                        sync = new CalendarSync(null, null, executor);
                    } else if (cClass.equals(COLLECTION_CLASS_CONTACTS)) {
                        ContactsSyncExecutor executor = new ContactsSyncExecutor();
                        sync = new ContactsSync(null, null, executor);
                    } else if (cClass.equals(COLLECTION_CLASS_TASKS)) {
                        TasksSyncExecutor executor = new TasksSyncExecutor();
                        sync = new TasksSync(null, null, executor);
                    } else {
                        throw new BinaryCodecException(cClass + " not supported");
                    }
                    while (bp.next() == START_TAG && NAMESPACE_AIRSYNC.equals(bp.getNamespace())) {
                        if (AIRSYNC_SYNCKEY.equals(bp.getName()))
                            bp.nextText();
                        else if (AIRSYNC_COLLECTIONID.equals(bp.getName()))
                            bp.nextText();
                        else if (AIRSYNC_STATUS.equals(bp.getName())) {
                            bp.nextIntegerContent();
                            break;
                        }
                    }
                    sync.parseCollectionResponse(bp, false);
                    bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
                }
                break;
            } else if (AIRSYNC_STATUS.equals(bp.getName())) {
                bp.nextIntegerContent();
                break;
            } else {
                bp.skipUnknownElement();
            }
        }
        bp.closeTag(); //Sync
    }
    
    private class EmailSyncExecutor implements EmailSyncServerCommandExcecutor {

        public void doServerAdd(String serverId, EmailAppData appData, InputStream in) 
                throws CommandCallbackException, IOException {
            ByteUtil.skip(in, Long.MAX_VALUE);
        }

        public void doServerChange(String serverId, boolean isRead)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerDelete(String serverId)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private class CalendarSyncExecutor implements CalendarSyncServerCommandExecutor {

        public void doServerAdd(String serverId, CalendarAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerChange(String serverId, CalendarAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerDelete(String serverId)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private class ContactsSyncExecutor implements ContactsSyncServerCommandExecutor {

        public void doServerAdd(String serverId, ContactAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerChange(String serverId, ContactAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerDelete(String serverId)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    private class TasksSyncExecutor implements TasksSyncServerCommandExecutor {

        public void doServerAdd(String serverId, CalendarAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerChange(String serverId, CalendarAppData appData)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }

        public void doServerDelete(String serverId)
                throws CommandCallbackException {
            // TODO Auto-generated method stub
            
        }
        
    }

}
