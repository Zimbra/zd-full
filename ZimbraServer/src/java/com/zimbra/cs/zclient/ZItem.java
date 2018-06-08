/*
 * 
 */

package com.zimbra.cs.zclient;

import com.zimbra.cs.zclient.event.ZModifyEvent;
import com.zimbra.common.service.ServiceException;

public interface ZItem {

    public String getId();

    //public ZMailbox getMailbox();

    public void modifyNotification(ZModifyEvent event) throws ServiceException;

}
