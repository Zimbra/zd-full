/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;

public class IMPresenceUpdateNotification extends IMNotification {

    IMAddr mFromAddr;
    IMPresence mPresence;
    
    IMPresenceUpdateNotification(IMAddr fromAddr, IMPresence presence)
    {
        mFromAddr = fromAddr;
        mPresence = presence;
    }
    
    public String toString() {
        return new Formatter().format("IMPresenceUpdateNotification: From: %s  Presence: %s", 
                mFromAddr, mPresence.toString()).toString();
    }
    
    public Element toXml(Element parent) {
        ZimbraLog.im.debug(this.toString());
        Element toRet = create(parent, IMConstants.E_PRESENCE);
        mPresence.toXml(toRet);
        toRet.addAttribute(IMConstants.A_FROM, mFromAddr.getAddr());
        return toRet;
    }
    
}
