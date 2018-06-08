/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;

import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class Ping extends Command {

    @Override
    protected void encodeRequest(BinarySerializer bs)
            throws BinaryCodecException, IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {
        // TODO Auto-generated method stub
        
    }

    //  <Ping xmlns="Ping:">
    //    <Status>1</Status>
    //  </Ping>
    
    //  <Ping xmlns="Ping:">
    //    <Status>2</Status>
    //    <Folders>
    //      <Folder>0286243fce792f4a82f3686ac614bc47-2c6f</Folder>
    //    </Folders>
    //  </Ping>

    //  <Ping xmlns="Ping:">
    //    <Status>5</Status>
    //    <HeartbeatInterval>120</HeartbeatInterval>
    //  </Ping>
    
    //  <Ping xmlns="Ping:">
    //    <HeartbeatInterval>480</HeartbeatInterval>
    //    <Folders>
    //      <Folder>
    //        <Id>2</Id>
    //        <Class>Contacts</Class>
    //      </Folder>
    //      <Folder>
    //        <Id>1</Id>
    //        <Class>Calendar</Class>
    //      </Folder>
    //      <Folder>
    //        <Id>5</Id>
    //        <Class>Email</Class>
    //      </Folder>
    //      <Folder>
    //        <Id>11</Id>
    //        <Class>Tasks</Class>
    //      </Folder>
    //    </Folders>
    //  </Ping>
    
    // <?xml version="1.0" encoding="utf-8"?>
    // <Ping xmlns="Ping">
    //    <Status>2</Status>
    //    <Folders>
    //        <Folder>2</Folder>
    //    </Folders>
    // </Ping>

    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException,
            BinaryCodecException, IOException {
        boolean readFolderText = true;
        bp.openTag(NAMESPACE_PING, PING_PING);
        while (bp.next() == START_TAG && NAMESPACE_PING.equals(bp.getNamespace())) {
            if (PING_STATUS.equals(bp.getName()))
                bp.nextIntegerContent();
            else if (PING_HEARTBEATINTERVAL.equals(bp.getName()))
                bp.nextIntegerContent();
            else if (PING_FOLDERS.equals(bp.getName())) {
                while (bp.next() == START_TAG && NAMESPACE_PING.equals(bp.getNamespace())) {
                    if (PING_FOLDER.equals(bp.getName())) {
                        while (bp.next() == START_TAG && NAMESPACE_PING.equals(bp.getNamespace())) {
                            readFolderText = false;
                            if (PING_ID.equals(bp.getName()))
                                bp.nextIntegerContent();
                            else if (PING_ID.equals(bp.getName()))
                                bp.nextText();
                            else
                                bp.skipUnknownElement();
                        }
                        if (readFolderText) {
                            bp.closeTag(); // Folder
                        } else {
                            // end of folder
                            bp.require(END_TAG, NAMESPACE_PING, PING_FOLDER);
                        }
                    } else 
                        bp.skipUnknownElement();
                }
                // end of folders
                bp.require(END_TAG, NAMESPACE_PING, PING_FOLDERS);
            } else 
                bp.skipUnknownElement();
        }
        bp.require(END_TAG, NAMESPACE_PING, PING_PING);
    }

}
