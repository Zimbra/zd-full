/*
 * 
 */
package com.zimbra.cs.nio.mina;

import com.zimbra.cs.nio.NioStatsMBean;
import org.apache.mina.common.IoSession;

import java.util.concurrent.atomic.AtomicLong;

class MinaStats implements NioStatsMBean {
    private final MinaServer server;
    private final AtomicLong totalSessions = new AtomicLong();
    private final AtomicLong activeSessions = new AtomicLong();
    private final AtomicLong receivedBytes = new AtomicLong();
    private final AtomicLong sentBytes = new AtomicLong();

    public MinaStats(MinaServer server) {
        this.server = server;
    }
    
    public long getTotalSessions() {
        return totalSessions.get();
    }

    public long getActiveSessions() {
        return activeSessions.get();
    }

    public long getReceivedBytes() {
        return receivedBytes.get();
    }

    public long getSentBytes() {
        return sentBytes.get();
    }

    public void sessionOpened() {
        activeSessions.incrementAndGet();
        totalSessions.incrementAndGet();
    }

    public void sessionClosed() {
        activeSessions.decrementAndGet();
    }

    public void bytesSent(int count) {
        sentBytes.addAndGet(count);
    }

    public void bytesReceived(int count) {
        receivedBytes.addAndGet(count);
    }

    public long getScheduledWriteBytes() {
        long total = 0;
        for (IoSession session : server.getSessions()) {
            total += session.getScheduledWriteBytes();
        }
        return total;
    }
}
