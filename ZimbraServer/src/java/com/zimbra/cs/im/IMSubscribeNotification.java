/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;

/**
 * Someone is trying to add us to their buddy list
 */
public class IMSubscribeNotification extends IMNotification {
    IMAddr mFromAddr;
    
    IMSubscribeNotification(IMAddr fromAddr) {
        mFromAddr = fromAddr;
    }
    
    public String toString() {
        return new Formatter().format("IMSubscribeNotification: From: %s", mFromAddr).toString();
    }

    public Element toXml(Element parent) {
        ZimbraLog.im.debug(this.toString());
        Element toRet = create(parent, IMConstants.E_SUBSCRIBE);
        toRet.addAttribute(IMConstants.A_FROM, mFromAddr.getAddr());
        return toRet;
    }
}
