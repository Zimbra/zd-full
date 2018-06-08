/*
 * 
 */

package com.zimbra.cs.nio;

import java.nio.ByteBuffer;

public class NioTextLineRequest implements NioRequest {
    private final LineBuffer mBuffer;

    public NioTextLineRequest() {
        mBuffer = new LineBuffer();
    }

    public void parse(ByteBuffer bb) {
        mBuffer.parse(bb);
    }

    public boolean isComplete() {
        return mBuffer.isComplete();
    }

    public String getLine() {
        return mBuffer.isComplete() ? mBuffer.toString() : null;
    }

    public String toString() {
        return getLine();
    }
}
