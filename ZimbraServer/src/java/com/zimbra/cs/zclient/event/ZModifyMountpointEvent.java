/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.zclient.ZJSONObject;
import org.json.JSONException;

public class ZModifyMountpointEvent extends ZModifyFolderEvent {


    public ZModifyMountpointEvent(Element e) throws ServiceException {
        super(e);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getOwnerDisplayName(String defaultValue) {
        return mFolderEl.getAttribute(MailConstants.A_OWNER_NAME, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getRemoteId(String defaultValue) {
        return mFolderEl.getAttribute(MailConstants.A_REMOTE_ID, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getOwnerId(String defaultValue) {
        return mFolderEl.getAttribute(MailConstants.A_ZIMBRA_ID, defaultValue);
    }

    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = super.toZJSONObject();
        if (getOwnerId(null) != null) zjo.put("ownerId", getOwnerId(null));
        if (getOwnerDisplayName(null) != null) zjo.put("ownerDisplayName", getOwnerDisplayName(null));
        if (getRemoteId(null) != null) zjo.put("remoteId", getRemoteId(null));
        return zjo;
    }
}
