/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.zimbra.common.service.ServiceException;
import com.zimbra.zimbrasync.wbxml.BinaryCodec;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public abstract class Command extends BinaryCodec {

    protected int status;

    public String getName() {
        return getClass().getSimpleName();
    }
    
    public void doCommand(SyncSettings syncSettings, String policyKey, boolean isDebugTraceOn) throws ServiceException, HttpStatusException, ResponseStatusException, CommandCallbackException, BinaryCodecException, IOException {
        new Request(syncSettings, policyKey).doRequest(this, isDebugTraceOn);
    }

    public void encodeRequest(OutputStream out, boolean isDebugTraceOn) throws BinaryCodecException, IOException {
        encodeRequest(new BinarySerializer(out, isDebugTraceOn));
    }

    protected abstract void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException;


    public void processResponse(InputStream in, int size, boolean isDebugTraceOn) throws ResponseStatusException, CommandCallbackException, BinaryCodecException, IOException {
        BinaryParser bp = new BinaryParser(in, isDebugTraceOn);
        try {
            parseResponse(bp);
        } catch (BinaryCodecException x) {
            //if we encounter any exception during parsing phase, we want to capture the raw PDU
            bp.logCodecError(in, size, x);
            throw x;
        }
        if (status != 1)
            handleStatusError();
    }

    public abstract void parseResponse(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException;

    protected abstract void handleStatusError() throws ResponseStatusException;
}
