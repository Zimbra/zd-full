/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;

public abstract class IMChatNotification extends IMNotification {
    
    IMAddr mFromAddr;
    String mThreadId;
    
    IMChatNotification(IMAddr from, String threadId) {
        mFromAddr = from;
        mThreadId = threadId;
    }
    

    public String toString() {
        return new Formatter().format("Addr: %s  Thread: %s",
                mFromAddr, mThreadId).toString();
    }

    /* (non-Javadoc)
    * @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element)
    */
    public Element toXml(Element parent) {
        parent.addAttribute(IMConstants.A_THREAD_ID, mThreadId);
        parent.addAttribute(IMConstants.A_ADDRESS, mFromAddr.getAddr());
        return parent;
    }
}
