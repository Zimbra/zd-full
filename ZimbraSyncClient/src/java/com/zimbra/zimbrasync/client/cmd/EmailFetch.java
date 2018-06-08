/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;


public class EmailFetch extends EmailSync {
    
    private String serverId;

    public EmailFetch(String collectionId, String clientSyncKey, EmailSyncServerCommandExcecutor serverCommandExecutor, String serverId) {
        super(collectionId, clientSyncKey, serverCommandExecutor);
        this.serverId = serverId;
    }

    //    <Sync xmlns="AirSync">
    //        <Collections>
    //            <Collection>
    //                <Class>Email</Class>
    //                <SyncKey>{7CC78172-E25A-3061-892A-9DD81E211151}6</SyncKey>
    //                <CollectionId>2</CollectionId>
    //                <DeletesAsMoves/>
    //                <Options>
    //                    <MIMESupport>2</MIMESupport>
    //                </Options>
    //                <Commands>
    //                    <Fetch>
    //                        <ServerId>290</ServerId>
    //                    </Fetch>
    //                </Commands>
    //            </Collection>
    //        </Collections>
    //    </Sync>

    @Override
    protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
        assert !clientSyncKey.equals("0");
        
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_SYNC);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONS);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTION);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_CLASS, collectionClass);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SYNCKEY, clientSyncKey);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_COLLECTIONID, collectionId);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_OPTIONS);
        bs.integerElement(NAMESPACE_AIRSYNC, AIRSYNC_MIMESUPPORT, 2); //always send mime
        bs.closeTag(); //Options
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_COMMANDS);
        bs.openTag(NAMESPACE_AIRSYNC, AIRSYNC_FETCH);
        bs.textElement(NAMESPACE_AIRSYNC, AIRSYNC_SERVERID, serverId);
        bs.closeTag(); //Fetch
        bs.closeTag(); //Commands;
        bs.closeTag(); //Collection
        bs.closeTag(); //Collections
        bs.closeTag(); //Sync
    }
}
