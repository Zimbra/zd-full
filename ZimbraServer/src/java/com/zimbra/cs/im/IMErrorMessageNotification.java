/*
 * 
 */
package com.zimbra.cs.im;

import org.xmpp.packet.PacketError;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMMessage.TextPart;

/**
 * 
 */
public class IMErrorMessageNotification extends IMBaseMessageNotification {
    
    private PacketError.Condition mErrorCondition = null;
    private IMMessage mErrorText;

    public IMErrorMessageNotification(String fromAddr, String threadId, boolean typing, long timestamp, String errorText, PacketError.Condition errorCondition) {
        super(fromAddr, threadId, typing, timestamp);
        mErrorCondition = errorCondition;
        mErrorText = new IMMessage(null, new TextPart("ERROR: "+errorText), false);
        mErrorText.setFrom(new IMAddr(fromAddr));
    }

    public Element toXml(Element parent) throws ServiceException {
        Element e = super.toXml(parent);
        
//        switch(mErrorCondition) {
//            case recipient_unavailable:
//                e.addAttribute(IMConstants.A_ERROR, PacketError.Condition.recipient_unavailable.name());
//                break;
//                
//        }
        if (mErrorCondition != null)
            e.addAttribute(IMConstants.A_ERROR, mErrorCondition.name());
        mErrorText.toXml(e);
        return e;
    }
}
