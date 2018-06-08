/*
 * 
 */
package com.zimbra.cs.im;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

/**
 * 
 */
public class IMPrivacyListNotification extends IMNotification {
    
    PrivacyList mList;
    
    IMPrivacyListNotification(PrivacyList list) { mList = list; }

    /* @see com.zimbra.cs.im.IMNotification#toXml(com.zimbra.common.soap.Element) */
    @Override
    public Element toXml(Element parent) throws ServiceException {
        Element e = this.create(parent, "privacy");
        return mList.toXml(e);
    }
    
    public String toString() { 
        try { 
            return toXml(null).toString(); 
        } catch (ServiceException ex) {
            ex.printStackTrace();
            return ex.toString(); 
        } 
    }
    
}
