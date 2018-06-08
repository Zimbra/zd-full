/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

public class ZModifyConversationEvent extends ZConversationSummaryEvent implements ZModifyItemEvent {

    public ZModifyConversationEvent(Element e) throws ServiceException {
        super(e);
    }
}
