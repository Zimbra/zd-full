/*
 * 
 */
package com.zimbra.cs.im;

import org.xmpp.packet.PacketError;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;

/**
 * 
 */
public class IMBaseMessageNotification extends IMNotification {
    
    private String mFromAddr;
    private String mThreadId;
    private long mTimestamp;
    private boolean mTyping;
    public IMBaseMessageNotification(String fromAddr, String threadId, boolean typing, long timestamp) {
        mFromAddr = fromAddr;
        mThreadId = threadId;
        mTyping = typing;
        mTimestamp = timestamp;
    }
    
    /* @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element) */
    @Override
    public Element toXml(Element parent) throws ServiceException {
        Element e = create(parent, IMConstants.E_MESSAGE);
        e.addAttribute(IMConstants.A_FROM, mFromAddr);
        e.addAttribute(IMConstants.A_THREAD_ID, mThreadId);
        if (mTyping)
            e.addElement(IMConstants.E_TYPING);
        e.addAttribute(IMConstants.A_TIMESTAMP, mTimestamp);
        return e;
    }

    public final String getFromAddr() { return mFromAddr; };
    public final String getThreadId() { return mThreadId; }
    public final boolean isTyping() { return mTyping; }
    public final long getTimestamp() { return mTimestamp; }
}
