/*
 * 
 */

package com.zimbra.cs.mina;

import java.io.IOException;

/**
 * Protocol handler for MINA-based server connections. The protocol handler
 * defines the action to take whenever a new connection is opened, is closed,
 * becomes idle, or a new request is received on the connection.
 */
public interface MinaHandler {
    /**
     * Called when a new connection has been opened.
     * 
     * @throws IOException if an I/O error occurs
     */
    void connectionOpened() throws IOException;

    /**
     * Called when the connection has been closed.
     *
     * @throws IOException if an I/O error occurs
     */
    void connectionClosed() throws IOException;

    /**
     * Called when the connection becomes idle after a specified period of
     * inactivity.
     * 
     * @throws IOException if an I/O error occurs
     */
    void connectionIdle() throws IOException;

    /**
     * Called when a new message has been received on the connection
     * \
     * @param msg the message that has been received
     * @throws IOException if an I/O error occurs
     */
    void messageReceived(Object msg) throws IOException;

    /**
     * Drop connection and wait up to 'timeout' milliseconds for last write
     * to complete before connection is closed.
     * 
     * @param timeout timeout grace per
     * @throws IOException if an I/O error occurs
     */
    void dropConnection(long timeout) throws IOException;
}
