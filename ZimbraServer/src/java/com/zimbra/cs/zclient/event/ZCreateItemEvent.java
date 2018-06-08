/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZItem;

public interface ZCreateItemEvent extends ZCreateEvent {

    public String getId() throws ServiceException;

    /**
     *
     * @return the ZItem, if this event contains the full item, NULL otherwise.
     *
     * @throws ServiceException on error
     */
    public ZItem getItem() throws ServiceException;
}
