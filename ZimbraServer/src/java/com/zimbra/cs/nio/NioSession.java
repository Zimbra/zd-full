/*
 * 
 */
package com.zimbra.cs.nio;

import javax.security.sasl.SaslServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public interface NioSession {
    NioServer getServer();
    InetSocketAddress getRemoteAddress();
    OutputStream getOutputStream();
    void startTls() throws IOException;
    void setMaxIdleSeconds(int secs);
    void startSasl(SaslServer sasl) throws IOException;
    void send(ByteBuffer bb) throws IOException;
    boolean drainWriteQueue(int threshold, long timeout);
    int getScheduledWriteBytes();
    void close();
    boolean isClosed();
}
