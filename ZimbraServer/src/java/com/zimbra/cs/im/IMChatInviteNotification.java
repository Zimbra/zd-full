/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;

public class IMChatInviteNotification extends IMChatNotification {
    String mInviteMessage;
    
    IMChatInviteNotification(IMAddr addr, String threadId, String inviteMessage) {
        super(addr, threadId);
        mInviteMessage = inviteMessage;
    }
    
    public String toString() {
        return new Formatter().format("IMChatInviteNotification: %s -- ", 
                    super.toString(), mInviteMessage).toString();
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element)
     */
     public Element toXml(Element parent) {
         Element toRet = create(parent, IMConstants.E_INVITED);
         super.toXml(toRet);
         toRet.setText(mInviteMessage);
         return toRet;
     }
}
