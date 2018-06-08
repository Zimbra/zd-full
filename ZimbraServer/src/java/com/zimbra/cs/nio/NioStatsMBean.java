/*
 * 
 */
package com.zimbra.cs.nio;

public interface NioStatsMBean {
    long getTotalSessions();
    long getActiveSessions();
    long getReceivedBytes();
    long getSentBytes();
    long getScheduledWriteBytes();
}
