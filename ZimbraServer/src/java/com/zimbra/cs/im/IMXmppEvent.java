/*
 * 
 */
package com.zimbra.cs.im;

import org.xmpp.packet.Packet;

import com.zimbra.common.service.ServiceException;

public class IMXmppEvent extends IMEvent {

    Packet mPacket;
    private boolean mIntercepted = false;; 
    
    IMXmppEvent(IMAddr target, Packet packet) {
        this(target, packet, false);
    }

    IMXmppEvent(IMAddr target, Packet packet, boolean intercepted) {
        super(target);
        mPacket = packet;
        mIntercepted = intercepted;
    }
    
    boolean isIntercepted() { return mIntercepted; }
    
    protected void handleTarget(IMPersona persona) throws ServiceException {
        if (mIntercepted) {
            persona.processIntercepted(mPacket);
        } else {
            persona.process(mPacket);
        }
    }
    
    public String toString() { 
        return "XMPPEvent: " + mPacket.toXML();
    }
    
}
