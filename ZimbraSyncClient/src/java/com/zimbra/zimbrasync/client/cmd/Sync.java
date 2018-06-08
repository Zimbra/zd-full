/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public abstract class Sync extends SyncCommand {
    
//    public static interface AirSyncAppData {
//        public void encode(BinarySerializer bs) throws BinaryCodecException, IOException;
//        public void parse(BinaryParser bp) throws BinaryCodecException, IOException;
//    }
    
    public static enum SyncError {
        //TODO
    };
    
    
    public abstract static class AirSyncCommand {
        String serverId;
        
        public AirSyncCommand(String serverId) {
            this.serverId = serverId;
        }
    }
    
    public abstract static class AirSyncClientCommand extends AirSyncCommand {
        int status;
        
        public AirSyncClientCommand(String serverId) {
            super(serverId);
        }
        
        public abstract void encode(BinarySerializer bs) throws BinaryCodecException, IOException;
        
        public abstract void processResponse() throws CommandCallbackException;
    }
    
    public abstract static class AirSyncServerCommand extends AirSyncCommand {
        public AirSyncServerCommand(String serverId) {
            super(serverId);
        }
        
        protected abstract void parse(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException;
        
        protected abstract void execute() throws CommandCallbackException;
    }
    
    public static interface AirSyncClientItem {
        public String getClientId();
    }
    
    public abstract static class AirSyncClientAdd extends AirSyncClientCommand {
        public static interface ClientAddResponseCallback {
            public void itemAdded(AirSyncClientItem clientItem, String serverId) throws CommandCallbackException;
            public void itemAddError(AirSyncClientItem clientItem, SyncError error) throws CommandCallbackException;
        }
        
        private AirSyncClientItem clientItem;
        private ClientAddResponseCallback callback;
        
        public AirSyncClientAdd(AirSyncClientItem clientItem, ClientAddResponseCallback callback) {
            super(null);
            this.clientItem = clientItem;
            this.callback = callback;
        }
        
        public String getClientId() {
            return clientItem.getClientId();
        }
        
        @Override
        public void encode(BinarySerializer bs) throws BinaryCodecException, IOException {
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_ADD);
            bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_CLIENTID, clientItem.getClientId());
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
            encodeApplicationData(bs);
            bs.closeTag(); //ApplicationData
            bs.closeTag(); //Add
        }
        
        protected abstract void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException;
        
        @Override
        public void processResponse() throws CommandCallbackException {
            if (status == 1)
                callback.itemAdded(clientItem, serverId);
            else
                callback.itemAddError(clientItem, null); //TODO
        }
    }
    
    public abstract static class AirSyncServerAdd extends AirSyncServerCommand {
        public AirSyncServerAdd(String serverId) {
            super(serverId);
        }
        
        @Override
        public void parse(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException {
            bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
            parseApplicationData(bp);
            bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
        }
        
        protected abstract void parseApplicationData(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException;
        
        protected boolean isFetchNeeded() {
            return false;
        }
    }
    
    public abstract static class AirSyncClientChange extends AirSyncClientCommand {
        public static interface ClientChangeResponseCallback {
            public void itemChanged(AirSyncClientItem clientItem) throws CommandCallbackException;
            public void itemChangeError(AirSyncClientItem clientItem, SyncError error) throws CommandCallbackException;
        }
        
        private AirSyncClientItem clientItem;
        private ClientChangeResponseCallback callback;
        
        public AirSyncClientChange(String serverId, AirSyncClientItem clientItem, ClientChangeResponseCallback callback) {
            super(serverId);
            this.clientItem = clientItem;
            this.callback = callback;
        }

        @Override
        public void encode(BinarySerializer bs) throws BinaryCodecException, IOException {
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_CHANGE);
            bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID, serverId);
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
            encodeApplicationData(bs);
            bs.closeTag(); //ApplicationData
            bs.closeTag(); //Change
        }
        
        protected abstract void encodeApplicationData(BinarySerializer bs) throws BinaryCodecException, IOException;
        
        @Override
        public void processResponse() throws CommandCallbackException {
            if (status == 1)
                callback.itemChanged(clientItem);
            else
                callback.itemChangeError(clientItem, null); //TODO
        }
    }
    
    public abstract static class AirSyncServerChange extends AirSyncServerCommand {
        public AirSyncServerChange(String serverId) {
            super(serverId);
        }
        
        @Override
        public void parse(BinaryParser bp) throws BinaryCodecException, IOException {
            bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
            parseApplicationData(bp);
            bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_APPLICATIONDATA);
        }
        
        protected abstract void parseApplicationData(BinaryParser bp) throws BinaryCodecException, IOException;
    }
    
    public static class AirSyncClientDelete extends AirSyncClientCommand {
        public static interface ClientDeleteResponseCallback {
            public void itemDeleted(String serverId) throws CommandCallbackException;
            public void itemDeleteError(String serverid, SyncError error) throws CommandCallbackException;
        }
        private ClientDeleteResponseCallback callback;
        
        public AirSyncClientDelete(String serverId, ClientDeleteResponseCallback callback) {
            super(serverId);
            this.callback = callback;
        }

        @Override
        public void encode(BinarySerializer bs) throws BinaryCodecException, IOException {
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_DELETE);
            bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID, serverId);
            bs.closeTag(); //Delete
        }
        
        @Override
        public void processResponse() throws CommandCallbackException {
            if (status == 1)
                callback.itemDeleted(serverId);
            else
                callback.itemDeleteError(serverId, null); //TODO
        }
    }
    
    public final static class AirSyncServerDelete extends AirSyncServerCommand {
        public interface Executor {
            public void doServerDelete(String serverId) throws CommandCallbackException;
        }
        
        private Executor executor;
        
        public AirSyncServerDelete(String serverId, Executor executor) {
            super(serverId);
            this.executor = executor;
        }
        
        @Override
        public void parse(BinaryParser bp) throws BinaryCodecException, IOException {
            assert false;
        }
        
        @Override
        protected void execute() throws CommandCallbackException {
            executor.doServerDelete(serverId);
        }
    }

    protected String collectionClass;
    protected String collectionId;
    
    private AirSyncServerDelete.Executor serverDeleteExecutor;
   
    private List<AirSyncClientCommand> clientCommands = new ArrayList<AirSyncClientCommand>();
    private Map<String, AirSyncClientAdd> clientAddsByClientId = new HashMap<String, AirSyncClientAdd>();
    private Map<String, AirSyncClientCommand> clientChangesByServerId = new HashMap<String, AirSyncClientCommand>(); //including deletes
    
    private List<String> fetchList = new ArrayList<String>();
    
    private boolean hasMore;
    
    public Sync(String collectionClass, String collectionId, String clientSyncKey, AirSyncServerDelete.Executor serverDeleteExecutor) {
        super(clientSyncKey);
        this.collectionClass = collectionClass;
        this.collectionId = collectionId;
        this.serverDeleteExecutor = serverDeleteExecutor;
    }
    
    int getWindowSize() {
        int windowSize = LC.data_source_eas_window_size.intValue();
        return windowSize > 0 && windowSize <= 100 ? windowSize : 50;
    }
    
    public List<String> getFetchList() {
        return fetchList;
    }
    
    public boolean hasMore() {
        return hasMore;
    }
    
    public void addClientAdd(AirSyncClientAdd add) {
        clientCommands.add(add);
        clientAddsByClientId.put(add.getClientId(), add);
    }
    
    public void addClientAdds(List<AirSyncClientAdd> clientAdds) {
        this.clientCommands.addAll(clientAdds);
        for (AirSyncClientAdd add : clientAdds)
            clientAddsByClientId.put(add.getClientId(), add);
    }
    
    public void addClientChange(AirSyncClientChange change) {
        clientCommands.add(change);
        clientChangesByServerId.put(change.serverId, change);
    }
    
    private void addClientChangesOrDeletes(List<? extends AirSyncClientCommand> clientCommands) {
        this.clientCommands.addAll(clientCommands);
        for (AirSyncClientCommand cmd : clientCommands)
            clientChangesByServerId.put(cmd.serverId, cmd);
    }
    
    public void addClientChanges(List<AirSyncClientChange> clientChanges) {
        addClientChangesOrDeletes(clientChanges);
    }
    
    public void addClientDelete(AirSyncClientDelete delete) {
        clientCommands.add(delete);
        clientChangesByServerId.put(delete.serverId, delete);
    }
    
    public void addClientDeletes(List<AirSyncClientDelete> clientDeletes) {
        addClientChangesOrDeletes(clientDeletes);
    }
    
    //<Sync xmlns="AirSync">
    //  <Collections>
    //      <Collection>
    //          <Class>Calendar</Class>
    //          <SyncKey>0</SyncKey>
    //          <CollectionId>0286243fce792f4a82f3686ac614bc47-2736</CollectionId>
    //      </Collection>
    //  </Collections>
    //</Sync>
    
    //<Sync xmlns="AirSync">
    //  <Collections>
    //      <Collection>
    //          <Class>Calendar</Class>
    //          <SyncKey>{407C7DEB-904F-48AF-8718-E1037C9C725F}1</SyncKey>
    //          <CollectionId>0286243fce792f4a82f3686ac614bc47-2736</CollectionId>
    //          <DeletesAsMoves/>
    //          <GetChanges/>
    //          <WindowSize>100</WindowSize>
    //          <Options>
    //              <FilterType>4</FilterType>
    //              <Truncation>4</Truncation>
    //              <RtfTruncation>4</RtfTruncation>
    //              <Conflict>1</Conflict>
    //          </Options>
    //      </Collection>
    //  </Collections>
    //</Sync>
    
    //<Sync xmlns="AirSync:" xmlns:A="POOMCONTACTS:">
    //    <Collections>
    //        <Collection>
    //            <Class>Contacts</Class>
    //            <SyncKey>{D7ECF3B7-8542-4AA6-8B3E-4EC83E1BC822}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2737</CollectionId>
    //            <DeletesAsMoves/>
    //            <GetChanges/>
    //            <WindowSize>100</WindowSize>
    //            <Options>
    //                <Truncation>4</Truncation>
    //                <RtfTruncation>4</RtfTruncation>
    //                <Conflict>1</Conflict>
    //            </Options>
    //            <Commands>
    //                <Add>
    //                    <ClientId>3400007900000002</ClientId>
    //                    <ApplicationData>
    //                        ... ...
    //                    </ApplicationData>
    //                </Add>
    //                <Change>
    //                    <ServerId>rid:ca8f94d7f36b83489b8b1e3bb4715c02000000510801</ServerId>
    //                    <ApplicationData>
    //                        ... ...
    //                    </ApplicationData>
    //                </Change>
    //                <Delete>
    //                    <ServerId>rid:ca8f94d7f36b83489b8b1e3bb4715c02000000510802</ServerId>
    //                </Delete>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    
    @Override
    protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONS);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_CLASS, collectionClass);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY, clientSyncKey);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONID, collectionId);
        if (!clientSyncKey.equals("0")) {
            //bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_DELETESASMOVES, 0); //deletes are always hard, we do move-to-trash separately
            bs.emptyElement(NAMESPACE_AIRSYNC, AIRSYNC_GETCHANGES);
            bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_WINDOWSIZE, getWindowSize());
            bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_OPTIONS);
            bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_FILTERTYPE, 0); //sync everything
            //bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_TRUNCATION, 9); //no truncation
            int mimeTruncation = LC.data_source_eas_mime_truncation.intValue();
            mimeTruncation = mimeTruncation >= 0 && mimeTruncation <= 8 ? mimeTruncation : 4; //4 is 10240
            bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_MIMETRUNCATION, mimeTruncation); //no truncation
            bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_MIMESUPPORT, 2); //always send mime
            bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_CONFLICT, 0); //client override server
            bs.closeTag(); //Options
            if (clientCommands != null && clientCommands.size() > 0) {
                bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COMMANDS);
                for (AirSyncClientCommand ascc : clientCommands)
                    ascc.encode(bs);
                bs.closeTag(); //Commands
            }
        }
        bs.closeTag(); //Collection
        bs.closeTag(); //Collections
        bs.closeTag(); //Sync
    }

    //<?xml version="1.0" encoding="utf-8"?><Sync xmlns="AirSync:" xmlns:A="POOMCONTACTS:">
    //    <Collections>
    //        <Collection>
    //            <Class>Contacts</Class>
    //            <SyncKey>{D7ECF3B7-8542-4AA6-8B3E-4EC83E1BC822}2</SyncKey>
    //            <CollectionId>0286243fce792f4a82f3686ac614bc47-2737</CollectionId>
    //            <Status>1</Status>
    //            <Responses>
    //                <Add>
    //                    <ClientId>3400007900000002</ClientId>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000009203</ServerId>
    //                    <Status>1</Status>
    //                </Add>
    //                <Change>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000009203</ServerId>
    //                    <Status>1</Status>
    //                </Change>
    //                <Delete>
    //                    <ServerId>rid:0286243fce792f4a82f3686ac614bc47000000009203</ServerId>
    //                    <Status>1</Status>
    //                </Delete>
    //            </Responses>
    //            <Commands>
    //                <Add>
    //                    <ServerId>rid:ca8f94d7f36b83489b8b1e3bb4715c02000000510801</ServerId>
    //                    <ApplicationData>
    //                        ... ...
    //                    </ApplicationData>
    //                </Add>
    //            </Commands>
    //        </Collection>
    //    </Collections>
    //</Sync>
    
    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONS);
        bp.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
        bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_CLASS);
        serverSyncKey = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY);
        bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONID);
        status = bp.nextIntegerElement(NAMESPACE_AIRSYNC, AIRSYNC_STATUS);
        
        parseCollectionResponse(bp, true);

        bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
        bp.closeTag(); //Collections
        bp.closeTag(); //Sync
        
        //server doesn't always send per item response, so if global status=1 we cover the items with no responses
        if (status == 1)
            for (AirSyncClientCommand cmd : clientChangesByServerId.values())
                if (cmd.status == 0) {
                    cmd.status = 1;
                    cmd.processResponse();
                }
    }
    
    /**
     * Parses the collection class in sync response.
     * @param bp binary parser
     * @param process whether to process the response while parsing
     */
    public void parseCollectionResponse(BinaryParser bp, boolean process)
        throws CommandCallbackException, BinaryCodecException, IOException {
        while (bp.next() == START_TAG && NAMESPACE_AIRSYNC.equals(bp.getNamespace())) {
            if (AIRSYNC_MOREAVAILABLE.equals(bp.getName())) {
                if (bp.isEmptyElementTag()) {
                    hasMore = true;
                    bp.closeTag();
                } else {
                    hasMore = (bp.nextIntegerContent() == 0) ? false : true;
                }
            } else if (AIRSYNC_RESPONSES.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_AIRSYNC.equals(bp.getNamespace())) {
                    if (AIRSYNC_ADD.equals(bp.getName())) {
                        String clientId = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_CLIENTID);
                        String serverId = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID);
                        int status = bp.nextIntegerElement(NAMESPACE_AIRSYNC, AIRSYNC_STATUS);
                        if (process) {
                            AirSyncClientAdd add = clientAddsByClientId.get(clientId);
                            add.serverId = serverId;
                            add.status = status;
                            add.processResponse();
                        }
                    } else if (AIRSYNC_FETCH.equals(bp.getName())) {
                        String serverId = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID);
                        if (bp.nextIntegerElement(NAMESPACE_AIRSYNC, AIRSYNC_STATUS) == 1) {
                            AirSyncServerAdd add = newServerAdd(serverId);
                            add.parse(bp);
                            if (process)
                                add.execute();
                        }
                    } else {
                        String serverId = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID);
                        int status = bp.nextIntegerElement(NAMESPACE_AIRSYNC, AIRSYNC_STATUS);
                        if (process) {
                            AirSyncClientCommand cmd = clientChangesByServerId.get(serverId);
                            cmd.status = status;
                            cmd.processResponse();
                        }
                    }
                    bp.closeTag(); //Add, Fetch, Change or Delete
                }
                bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_RESPONSES);
            } else if (AIRSYNC_COMMANDS.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_AIRSYNC.equals(bp.getNamespace())) {
                    String verb = bp.getName();
                    String serverId = bp.nextTextElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID);
                    if (AIRSYNC_ADD.equals(verb)) {
                        AirSyncServerAdd add = newServerAdd(serverId);
                        add.parse(bp);
                        if (process) {
                            add.execute();
                            if (add.isFetchNeeded())
                                fetchList.add(serverId);
                        }
                    } else if (AIRSYNC_CHANGE.equals(verb)) {
                        AirSyncServerChange change =  newServerChange(serverId);
                        change.parse(bp);
                        if (process)
                            change.execute();
                    } else if (AIRSYNC_DELETE.equals(verb)) {
                        if (process)
                            newServerDelete(serverId).execute();
                    } else
                        throw new BinaryCodecException("unknown verb " + verb);
                    bp.closeTag(); //Add, Change or Delete
                }
                bp.require(END_TAG, NAMESPACE_AIRSYNC, AIRSYNC_COMMANDS);
            } else
                throw new BinaryCodecException("unexpected tag: " + bp.getName());
        } 
    }
    
    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub

    }
    
    @Override
    public String getName() {
        return "Sync";
    }
    
    protected abstract AirSyncServerAdd newServerAdd(String serverId);
    
    protected abstract AirSyncServerChange newServerChange(String serverId);
    
    private AirSyncServerDelete newServerDelete(String serverId) {
        return new AirSyncServerDelete(serverId, serverDeleteExecutor);
    }
}
