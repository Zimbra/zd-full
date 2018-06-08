/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.data.ContactAppData;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public final class ContactsSync extends Sync {
    
    public static final class ContactsSyncClientAdd extends AirSyncClientAdd  {
        private ContactAppData appData;
        
        public ContactsSyncClientAdd(AirSyncClientItem clientItem, ClientAddResponseCallback callback, ContactAppData appData) {
            super(clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encode(bs);
        }
    }

    public static final class ContactsSyncClientChange extends AirSyncClientChange  {
        private ContactAppData appData;
        
        public ContactsSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback, ContactAppData appData) {
            super(serverId, clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encode(bs);
        }
    }
    
    public static final class ContactsSyncServerAdd extends AirSyncServerAdd {
        public static interface Executor {
            public void doServerAdd(String serverId, ContactAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private ContactAppData appData;
        
        public ContactsSyncServerAdd(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            appData = new ContactAppData();
            appData.parse(bp);
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerAdd(serverId, appData);
        }
    }
    
    public static final class ContactsSyncServerChange extends AirSyncServerChange {
        public static interface Executor {
            public void doServerChange(String serverId, ContactAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private ContactAppData appData;
        
        public ContactsSyncServerChange(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            appData = new ContactAppData();
            appData.parse(bp);
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerChange(serverId, appData);
        }
    }

    public interface ContactsSyncServerCommandExecutor extends ContactsSyncServerAdd.Executor, ContactsSyncServerChange.Executor, AirSyncServerDelete.Executor {}
    
    private static final String COLLECTION_CLASS = "Contacts";
    
    private ContactsSyncServerCommandExecutor serverCommandExecutor;
    
    public ContactsSync(String collectionId, String clientSyncKey, ContactsSyncServerCommandExecutor serverCommandExecutor) {
        super(COLLECTION_CLASS, collectionId, clientSyncKey, serverCommandExecutor);
        this.serverCommandExecutor = serverCommandExecutor;
    }
    
    @Override
    protected AirSyncServerAdd newServerAdd(String serverId) {
        return new ContactsSyncServerAdd(serverId, serverCommandExecutor);
    }
    
    @Override
    protected AirSyncServerChange newServerChange(String serverId) {
        return new ContactsSyncServerChange(serverId, serverCommandExecutor);
    }
}
