/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZItem;
import com.zimbra.cs.zclient.ZMountpoint;

public class ZCreateMountpointEvent implements ZCreateItemEvent {

    protected ZMountpoint mMountpoint;

    public ZCreateMountpointEvent(ZMountpoint mountpoint) throws ServiceException {
        mMountpoint = mountpoint;
    }

    /**
     * @return id of created mountpoint
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mMountpoint.getId();
    }

    public ZItem getItem() throws ServiceException {
        return mMountpoint;
    }

    public ZMountpoint getMountpoint() {
        return mMountpoint;
    }
    
    public String toString() {
    	return mMountpoint.toString();
    }
}
