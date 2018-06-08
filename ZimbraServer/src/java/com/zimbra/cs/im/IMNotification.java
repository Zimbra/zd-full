/*
 * 
 */
package com.zimbra.cs.im;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

public abstract class IMNotification {
    abstract public Element toXml(Element parent) throws ServiceException;
    
    protected static Element create(Element parent, String typeName) {
        return parent.addElement("n").addAttribute("type", typeName);
    }
}
