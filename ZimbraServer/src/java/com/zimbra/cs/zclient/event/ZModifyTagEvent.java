/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.zclient.ToZJSONObject;
import com.zimbra.cs.zclient.ZJSONObject;
import com.zimbra.cs.zclient.ZTag.Color;
import org.json.JSONException;

public class ZModifyTagEvent implements ZModifyItemEvent, ToZJSONObject {

    protected Element mTagEl;

    public ZModifyTagEvent(Element e) throws ServiceException {
        mTagEl = e;
    }

    /**
     * @return folder id of modified tag
     * @throws com.zimbra.common.service.ServiceException
     */
    public String getId() throws ServiceException {
        return mTagEl.getAttribute(MailConstants.A_ID);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getName(String defaultValue) {
        return mTagEl.getAttribute(MailConstants.A_NAME, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new color, or default value.
     */
    public Color getColor(Color defaultValue) {
        String newColor = mTagEl.getAttribute(MailConstants.A_RGB, null);
        if (newColor != null) {
                return Color.rgbColor.setRgbColor(newColor);
        } else {
            String s = mTagEl.getAttribute(MailConstants.A_COLOR, null);
            if (s != null) {
                try {
                    return Color.values()[(byte)Long.parseLong(s)];
                } catch (NumberFormatException se) {
                    return defaultValue;
                }
            }
        }
        return defaultValue;
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new unread count, or defaultVslue if unchanged
     * @throws com.zimbra.common.service.ServiceException on error
     */
    public int getUnreadCount(int defaultValue) throws ServiceException {
        return (int) mTagEl.getAttributeLong(MailConstants.A_UNREAD, defaultValue);
    }

    public ZJSONObject toZJSONObject() throws JSONException {
        try {
            ZJSONObject zjo = new ZJSONObject();
            zjo.put("id", getId());
            String name = getName(null);
            if (name != null) zjo.put("name", name);
            if (getColor(null) != null) zjo.put("color", getColor(null).name());
            if (getUnreadCount(-1) != -1) zjo.put("unreadCount", getUnreadCount(-1));
            return zjo;
        } catch (ServiceException se) {
            throw new JSONException(se);
        }
    }

    public String toString() {
        try {
            return String.format("[ZModifyTagEvent %s]", getId());
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
    }

    public String dump() {
        return ZJSONObject.toString(this);
    }
}
