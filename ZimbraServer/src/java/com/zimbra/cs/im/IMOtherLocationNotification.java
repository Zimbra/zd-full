/*
 * 
 */
package com.zimbra.cs.im;

import java.util.Formatter;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.util.ZimbraLog;

/**
 * "Your account has logged in from another location"
 */
public class IMOtherLocationNotification extends IMNotification {
    
    String mServiceName;
    String mUsername;
    
    IMOtherLocationNotification(String serviceName, String username) {
        mServiceName = serviceName;
        mUsername = username;
    }
    
    public String toString() {
        return new Formatter().format("IMOtherLocationNotification(%s, State=%s)",
            mServiceName, mUsername).toString();
    }
    
    /* @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element) */
    @Override
    public Element toXml(Element parent) throws ServiceException {
        ZimbraLog.im.debug(this.toString());
        Element toRet = create(parent, IMConstants.E_OTHER_LOCATION);
        toRet.addAttribute(IMConstants.A_SERVICE, mServiceName); 
        toRet.addAttribute(IMConstants.A_USERNAME, mUsername);
        return toRet;
    }

}
