/*
 * 
 */

package com.zimbra.cs.zclient;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

public class ZAppointment extends ZCalendarItem {
    
    public ZAppointment(Element e) throws ServiceException {
        super(e);
    }
}
