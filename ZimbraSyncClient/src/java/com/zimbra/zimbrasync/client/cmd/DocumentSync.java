/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class DocumentSync extends Sync {

    public static class DocumentAppData {
        
    }
    
    public static final class DocumentSyncClientAdd extends AirSyncClientAdd  {
        private DocumentAppData appData;
        
        public DocumentSyncClientAdd(AirSyncClientItem clientItem, ClientAddResponseCallback callback, DocumentAppData appData) {
            super(clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            //TODO
        }
    }

    public static final class DocumentSyncClientChange extends AirSyncClientChange  {
        private DocumentAppData appData;
        
        public DocumentSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback, DocumentAppData appData) {
            super(serverId, clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            //TODO
        }
    }
    
    public static final class DocumentSyncServerAdd extends AirSyncServerAdd {

        public static interface Executor {
            public void doServerAdd(String serverId, DocumentAppData appData);
        }
        
        private Executor executor;
        private DocumentAppData appData;
        
        public DocumentSyncServerAdd(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerAdd(serverId, appData);
        }
    }
    
    public static final class DocumentSyncServerChange extends AirSyncServerChange {

        public static interface Executor {
            public void doServerChange(String serverId, DocumentAppData appData);
        }
        
        private Executor executor;
        private DocumentAppData appData;
        
        public DocumentSyncServerChange(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerChange(serverId, appData);
        }
    }

    public interface DocumentSyncServerCommandExecutor extends DocumentSyncServerAdd.Executor, DocumentSyncServerChange.Executor, AirSyncServerDelete.Executor {}
    
    private static final String COLLECTION_CLASS = "Document";
    
    private DocumentSyncServerCommandExecutor serverCommandExecutor;
    
    public DocumentSync(String collectionId, String clientSyncKey, DocumentSyncServerCommandExecutor serverCommandExecutor) {
        super(COLLECTION_CLASS, collectionId, clientSyncKey, serverCommandExecutor);
        this.serverCommandExecutor = serverCommandExecutor;
    }
    
    @Override
    protected AirSyncServerAdd newServerAdd(String serverId) {
        return new DocumentSyncServerAdd(serverId, serverCommandExecutor);
    }
    
    @Override
    protected AirSyncServerChange newServerChange(String serverId) {
        return new DocumentSyncServerChange(serverId, serverCommandExecutor);
    }
}
