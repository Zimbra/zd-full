/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.data.CalendarAppData;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class TasksSync extends Sync {
    
    public static final class TasksSyncClientAdd extends AirSyncClientAdd  {
        private CalendarAppData appData;
        
        public TasksSyncClientAdd(AirSyncClientItem clientItem, ClientAddResponseCallback callback, CalendarAppData appData) {
            super(clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encodeTask(bs, true, false, -1);
        }
    }

    public static final class TasksSyncClientChange extends AirSyncClientChange  {
        private CalendarAppData appData;
        
        public TasksSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback, CalendarAppData appData) {
            super(serverId, clientItem, callback);
            this.appData = appData;
        }

        @Override
        protected void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException {
            appData.encodeTask(bs, true, false, -1);
        }
    }
    
    public static final class TasksSyncServerAdd extends AirSyncServerAdd {
        public static interface Executor {
            public void doServerAdd(String serverId, CalendarAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private CalendarAppData appData;
        
        public TasksSyncServerAdd(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            appData = new CalendarAppData();
            appData.parseTask(bp);
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerAdd(serverId, appData);
        }
    }
    
    public static final class TasksSyncServerChange extends AirSyncServerChange {

        public static interface Executor {
            public void doServerChange(String serverId, CalendarAppData appData) throws CommandCallbackException;
        }
        
        private Executor executor;
        private CalendarAppData appData;
        
        public TasksSyncServerChange(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }

        @Override
        protected void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException {
            appData = new CalendarAppData();
            appData.parseTask(bp);
        }

        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerChange(serverId, appData);
        }
    }

    public interface TasksSyncServerCommandExecutor extends TasksSyncServerAdd.Executor, TasksSyncServerChange.Executor, AirSyncServerDelete.Executor {}
    
    private static final String COLLECTION_CLASS = "Tasks";
    
    private TasksSyncServerCommandExecutor serverCommandExecutor;
    
    public TasksSync(String collectionId, String clientSyncKey, TasksSyncServerCommandExecutor serverCommandExecutor) {
        super(COLLECTION_CLASS, collectionId, clientSyncKey, serverCommandExecutor);
        this.serverCommandExecutor = serverCommandExecutor;
    }
    
    @Override
    protected AirSyncServerAdd newServerAdd(String serverId) {
        return new TasksSyncServerAdd(serverId, serverCommandExecutor);
    }
    
    @Override
    protected AirSyncServerChange newServerChange(String serverId) {
        return new TasksSyncServerChange(serverId, serverCommandExecutor);
    }
}
