/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.soap.Element;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZItem;

public class ZCreateConversationEvent extends ZConversationSummaryEvent implements ZCreateItemEvent {

    public ZCreateConversationEvent(Element e) throws ServiceException {
        super(e);
    }


    public ZItem getItem() throws ServiceException {
        return null;
    }
}
