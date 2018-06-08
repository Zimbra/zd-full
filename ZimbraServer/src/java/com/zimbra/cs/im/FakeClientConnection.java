/*
 * 
 */
package com.zimbra.cs.im;

import java.net.InetAddress;

import org.jivesoftware.wildfire.auth.UnauthorizedException;
import org.jivesoftware.wildfire.net.VirtualConnection;
import org.xmpp.packet.Packet;

public class FakeClientConnection extends VirtualConnection {
    
    IMAddr mAddr;
    
    FakeClientConnection(IMPersona persona) {
        mAddr = persona.getAddr();
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.net.VirtualConnection#closeVirtualConnection()
     */
    public void closeVirtualConnection() {
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.Connection#deliver(org.xmpp.packet.Packet)
     */
    public void deliver(Packet packet) throws UnauthorizedException {
        IMXmppEvent imXmppEvent = new IMXmppEvent(mAddr, packet);
        IMRouter.getInstance().postEvent(imXmppEvent);
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.Connection#deliverRawText(java.lang.String)
     */
    public void deliverRawText(String text) {
        // ignored for now
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.Connection#getInetAddress()
     */
    public InetAddress getInetAddress() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.jivesoftware.wildfire.Connection#systemShutdown()
     */
    public void systemShutdown() {
    }
}
