/*
 * 
 */
package com.zimbra.cs.milter;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.zimbra.cs.mina.MinaStats;

public class MinaMilterDecoder extends CumulativeProtocolDecoder {
    private final MinaStats stats;
    
    MinaMilterDecoder(MinaStats stats) {
        this.stats = stats;
    }
    
    @Override public boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) {
        if (!in.prefixedDataAvailable(4))
            return false;
        
        int len = in.getInt();
        byte cmd = in.get();
        byte[] data = null;
        if (len > 1) {
            data = new byte[len - 1];
            in.get(data);
        }
        MilterPacket packet = new MilterPacket(len, cmd, data);
        out.write(packet);
        
        if (stats != null) {
            stats.receivedBytes.addAndGet(len + 4);
        }
        return true;
    }
}
