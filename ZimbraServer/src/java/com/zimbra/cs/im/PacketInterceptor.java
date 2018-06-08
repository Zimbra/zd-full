/*
 * 
 */
package com.zimbra.cs.im;

import org.jivesoftware.wildfire.Session;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;

import com.zimbra.common.util.ZimbraLog;

public class PacketInterceptor implements org.jivesoftware.wildfire.interceptor.PacketInterceptor {

    public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed) /* throws PacketRejectedException */ {
        if (processed && packet instanceof Message) {
            if (session.getAddress().getNode() != null) {
                ZimbraLog.im_intercept.debug("Session "+ session.toString() +" Intercepting " + (incoming ? "INCOMING " : "OUTGOING ") + (processed ? "PROCESSED " : "NOT PROCESSED ") +" packet: "+packet.toString());
                String addr = session.getAddress().toBareJID().toString();
                IMXmppEvent imXmppEvent = new IMXmppEvent(new IMAddr(addr), packet, true);
                IMRouter.getInstance().postEvent(imXmppEvent);
            } 
        }
    }
}
