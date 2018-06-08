/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZItem;

public class ZCreateFolderEvent implements ZCreateItemEvent {

    protected ZFolder mFolder;

    public ZCreateFolderEvent(ZFolder folder) throws ServiceException {
        mFolder = folder;
    }

    /**
     * @return id of created folder
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mFolder.getId();
    }

    public ZItem getItem() throws ServiceException {
        return mFolder;
    }

    public ZFolder getFolder() {
        return mFolder;
    }
    
    public String toString() {
    	return mFolder.toString();
    }
}
