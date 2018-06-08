/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;

public class ZModifyVoiceMailItemFolderEvent implements ZModifyItemFolderEvent {
	private String mFolderId;

	public ZModifyVoiceMailItemFolderEvent(String folderId) throws ServiceException {
		mFolderId = folderId;
	}

	public String getFolderId(String defaultValue) throws ServiceException {
		return mFolderId;
	}
}
