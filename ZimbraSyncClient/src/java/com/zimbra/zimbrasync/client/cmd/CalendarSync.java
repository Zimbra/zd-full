/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.data.CalendarAppData;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class CalendarSync extends Sync {
    
    public static final class CalendarSyncClientAdd extends AirSyncClientAdd {
        private CalendarAppData appData;

        public CalendarSyncClientAdd(AirSyncClientItem clientItem, ClientAddResponseCallback callback, CalendarAppData appData) {
            super(clientItem, callback);
            this.appData = appData;
        }
        
        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encode(bs);
        }
    }
    
    public static final class CalendarSyncClientChange extends AirSyncClientChange {
        private CalendarAppData appData;

        public CalendarSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback, CalendarAppData appData) {
            super(serverId, clientItem, callback);
            this.appData = appData;
        }
        
        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encode(bs);
        }
    }
    
    public static final class CalendarSyncServerAdd extends AirSyncServerAdd {
        public interface Executor {
            public void doServerAdd(String serverId, CalendarAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private CalendarAppData appData;
        
        public CalendarSyncServerAdd(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException {
            appData = new CalendarAppData();
            appData.parse(bp);
        }
        
        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerAdd(serverId, appData);
        }
    }
    
    public static final class CalendarSyncServerChange extends AirSyncServerChange {
        public interface Executor {
            public void doServerChange(String serverId, CalendarAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private CalendarAppData appData;
        
        public CalendarSyncServerChange(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            appData = new CalendarAppData();
            appData.parse(bp);
        }
        
        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerChange(serverId, appData);
        }
    }
    
    public interface CalendarSyncServerCommandExecutor extends CalendarSyncServerAdd.Executor, CalendarSyncServerChange.Executor, AirSyncServerDelete.Executor {}
    
    private static final String COLLECTION_CLASS = "Calendar";
    
    private CalendarSyncServerCommandExecutor serverCommandExecutor;
    
    public CalendarSync(String collectionId, String clientSyncKey, CalendarSyncServerCommandExecutor serverCommandExecutor) {
        super(COLLECTION_CLASS, collectionId, clientSyncKey, serverCommandExecutor);
        this.serverCommandExecutor = serverCommandExecutor;
    }
    
    @Override
    protected AirSyncServerAdd newServerAdd(String serverId) {
        return new CalendarSyncServerAdd(serverId, serverCommandExecutor);
    }
    
    @Override
    protected AirSyncServerChange newServerChange(String serverId) {
        return new CalendarSyncServerChange(serverId, serverCommandExecutor);
    }
}
