/*
 * 
 */
package com.zimbra.cs.milter;

import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.common.ByteBuffer;

import com.zimbra.cs.mina.MinaStats;

public class MinaMilterEncoder extends ProtocolEncoderAdapter {
    private final MinaStats stats;
    
    MinaMilterEncoder(MinaStats stats) {
        this.stats = stats;
    }
    
    @Override public void encode(IoSession session, Object msg, ProtocolEncoderOutput out) {
        MilterPacket packet = (MilterPacket) msg;
        
        ByteBuffer buffer = ByteBuffer.allocate(4 + packet.getLength(), false);
        buffer.setAutoExpand(true);
        buffer.putInt(packet.getLength());
        buffer.put(packet.getCommand());       
        byte[] data = packet.getData();
        if (data != null && data.length > 0)
            buffer.put(data);
        buffer.flip();
        out.write(buffer);
        
        if (stats != null) {
            stats.sentBytes.addAndGet(buffer.capacity());
        }
    }
}
