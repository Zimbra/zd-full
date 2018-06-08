/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;

public interface ZModifyItemEvent extends ZModifyEvent {

    public String getId() throws ServiceException;

}
