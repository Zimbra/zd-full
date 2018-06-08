/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;

public class ZModifyVoiceMailItemEvent implements ZModifyItemEvent {
	private String mId;
	private boolean mIsHeard;
	private boolean mMadeChange;

	public ZModifyVoiceMailItemEvent(String id, boolean isHeard) throws ServiceException {
		mId = id;
		mIsHeard = isHeard;
		mMadeChange = false;
	}

	/**
	 * @return id
	 */
	public String getId() throws ServiceException {
		return mId;
	}

	/**
	 * @return true if item has been heard
	 */
	public boolean getIsHeard() {
		return mIsHeard;
	}

	/**
	 * Makes note that something actually changed. Used when marking (un)heard
	 * so that we can try to keep track of the folder's unheard count,
	 * which is never updated by the server.
	 */
	public void setMadeChange() {
		mMadeChange = true;
	}

	/**
	 * Returns true if something actually changed.
	 */
	public boolean getMadeChange() {
		return mMadeChange;
	}
}
