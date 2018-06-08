/*
 * 
 */
package org.jivesoftware.wildfire.net;

interface NioCompletionHandler {
    void nioReadCompleted(org.apache.mina.common.ByteBuffer buf);
    void nioClosed();
}
