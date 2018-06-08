/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;
import java.util.List;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMChat.MucStatusCode;

public class IMLeftChatNotification extends IMChatNotification {
    
    List<MucStatusCode> statusCodes;
    boolean isMe;

    IMLeftChatNotification(IMAddr from, boolean isMe, String threadId, List<MucStatusCode> statusCodes) {
        super(from, threadId);
        this.statusCodes = statusCodes;
        this.isMe = isMe;
    }
    
    public String toString() {
        return new Formatter().format("IMLeftChatNotification: %s", super.toString()).toString();
    }
    
    /* (non-Javadoc)
     * @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element)
     */
    public Element toXml(Element parent) {
        Element toRet = create(parent, IMConstants.E_LEFTCHAT);
        super.toXml(toRet);
        
        if (isMe) 
            toRet.addAttribute("me",true);

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
