/*
 * 
 */

package com.zimbra.cs.mina;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/*
 * MINA protocol codec factory. Decodes request bytes and then passes complete
 * request to MINA handler.
 */
public abstract class MinaCodecFactory implements ProtocolCodecFactory {
    protected MinaCodecFactory() {
    }

    public ProtocolEncoder getEncoder() {
        return new ProtocolEncoderAdapter() {
            public void encode(IoSession session, Object msg, ProtocolEncoderOutput out) {
                if (msg instanceof ByteBuffer) {
                    ByteBuffer bb = (ByteBuffer) msg;
                    out.write(bb);
                }
            }
        };
    }
}
