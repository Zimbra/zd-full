/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;
import java.util.List;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMChat.MucStatusCode;
import com.zimbra.cs.im.IMChat.Participant;

public class IMChatPresenceNotification extends IMChatNotification {
    private Participant part;
    private boolean entered;
    List<MucStatusCode> statusCodes;
    
    IMChatPresenceNotification(IMAddr addr, String threadId, boolean entered, Participant part, List<MucStatusCode> statusCodes) {
        super(addr, threadId);
        this.part = part;
        this.entered = entered;
        this.statusCodes = statusCodes;
    }

    public String toString() {
        return new Formatter().format("IMChatPresenceNotification: %s, %s", 
                                      super.toString(), part.toString()).toString();
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element)
     */
    public Element toXml(Element parent) {
        Element toRet;
        if (entered)
            toRet = create(parent, IMConstants.E_ENTEREDCHAT);
        else
            toRet = create(parent, IMConstants.E_CHATPRESENCE);
        super.toXml(toRet);
        part.toXML(toRet);
        
        StringBuilder errors = new StringBuilder();
        StringBuilder status = new StringBuilder();
        
        for (MucStatusCode code : statusCodes) {
            if (code.isError()) {
                if (errors.length() > 0)
                    errors.append(",");
                errors.append(code.name());
            } else {
                if (status.length() > 0)
                    status.append(",");
                status.append(code.name());
            }
        }
        if (status.length() > 0)
            toRet.addAttribute("status", status.toString());
        if (errors.length() > 0)
            toRet.addAttribute("error", errors.toString()); 
            
        return toRet;
    }
}
