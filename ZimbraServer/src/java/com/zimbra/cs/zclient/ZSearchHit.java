/*
 * 
 */

package com.zimbra.cs.zclient;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.event.ZModifyEvent;

public interface ZSearchHit extends ToZJSONObject {
    String getId();
    String getSortField();
    void modifyNotification(ZModifyEvent event) throws ServiceException;
}
