/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;

public class IMMessageNotification extends IMBaseMessageNotification {
    
    private IMMessage mMessage;
    private int mSeqNo;
    private String mToAddr;
    
    public IMMessageNotification(IMAddr fromAddr, String threadId, IMMessage message, int seqNo) {
        super(fromAddr.toString(), threadId, message.isTyping(), message.getTimestamp());
        mMessage = message;
        mSeqNo = seqNo;
        try { mToAddr = message.getTo().toString(); } catch (Exception e) {} // why in exception? figure out and comment me!  
    }
    
    public Element toXml(Element parent) throws ServiceException {
        Element e = super.toXml(parent);
        if (mToAddr != null) 
            e.addAttribute(IMConstants.A_TO, mToAddr);
        e.addAttribute(IMConstants.A_SEQ, mSeqNo);
        mMessage.toXml(e);
        return e;
    }
    
    public String toString() {
        return new Formatter().format("IMSendMessageEvent: %s --> thread %s Message=%s", 
                getFromAddr(), getThreadId(), mMessage.toString()).toString();
    }
    
    public final String getToAddr() { return mToAddr; }
}
