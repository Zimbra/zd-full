/*
 * 
 */
package com.zimbra.cs.nio;

import java.nio.ByteBuffer;

public interface NioDecoder {
    void decode(ByteBuffer bb, Output out);

    public interface Output {
        void write(Object obj);
    }
}
