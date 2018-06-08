/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZItem;
import com.zimbra.cs.zclient.ZSearchFolder;

public class ZCreateSearchFolderEvent implements ZCreateItemEvent {

    protected ZSearchFolder mSearchFolder;

    public ZCreateSearchFolderEvent(ZSearchFolder searchFolder) throws ServiceException {
        mSearchFolder = searchFolder;
    }

    /**
     * @return id of created search folder.
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mSearchFolder.getId();
    }

    public ZItem getItem() throws ServiceException {
        return mSearchFolder;
    }

    public ZSearchFolder getSearchFolder() {
        return mSearchFolder;
    }
    
    public String toString() {
    	return mSearchFolder.toString();
    }
}
