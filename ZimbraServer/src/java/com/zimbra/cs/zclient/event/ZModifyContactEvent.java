/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

public class ZModifyContactEvent extends ZContactEvent implements ZModifyItemEvent, ZModifyItemFolderEvent {

    public ZModifyContactEvent(Element e) throws ServiceException {
        super(e);
    }
}
