/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;

public interface ZModifyItemFolderEvent extends ZModifyEvent {

    public String getFolderId(String defaultValue) throws ServiceException;

}
