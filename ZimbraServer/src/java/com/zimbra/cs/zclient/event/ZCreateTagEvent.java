/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZItem;
import com.zimbra.cs.zclient.ZTag;

public class ZCreateTagEvent implements ZCreateItemEvent {

    protected ZTag mTag;

    public ZCreateTagEvent(ZTag tag) throws ServiceException {
        mTag = tag;
    }

    /**
     * @return tag id of created tag.
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mTag.getId();
    }

    public ZItem getItem() throws ServiceException {
        return mTag;
    }

    public ZTag getTag() {
        return mTag;
    }
    
    public String toString() {
    	return mTag.toString();
    }
}
