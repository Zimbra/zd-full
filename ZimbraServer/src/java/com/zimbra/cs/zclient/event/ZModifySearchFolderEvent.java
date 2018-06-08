/*
 * 
 */

package com.zimbra.cs.zclient.event;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.zclient.ToZJSONObject;
import com.zimbra.cs.zclient.ZJSONObject;
import com.zimbra.cs.zclient.ZMailbox.SearchSortBy;
import org.json.JSONException;

public class ZModifySearchFolderEvent extends ZModifyFolderEvent implements ToZJSONObject {


    public ZModifySearchFolderEvent(Element e) throws ServiceException {
        super(e);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getQuery(String defaultValue) {
        return mFolderEl.getAttribute(MailConstants.A_QUERY, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public String getTypes(String defaultValue) {
        return mFolderEl.getAttribute(MailConstants.A_SEARCH_TYPES, defaultValue);
    }

    /**
     * @param defaultValue value to return if unchanged
     * @return new name or defaultValue if unchanged
     */
    public SearchSortBy getSortBy(SearchSortBy defaultValue) {
        try {
            String newSort = mFolderEl.getAttribute(MailConstants.A_SORTBY, null);
            return newSort == null ? defaultValue : SearchSortBy.fromString(newSort);
        } catch (ServiceException se) {
            return defaultValue;
        }
    }
    
    public ZJSONObject toZJSONObject() throws JSONException {
        ZJSONObject zjo = super.toZJSONObject();
        if (getQuery(null) != null) zjo.put("query", getQuery(null));
        if (getTypes(null) != null) zjo.put("types", getTypes(null));
        if (getSortBy(null) != null) zjo.put("sortBy", getSortBy(null).name());
        return zjo;
    }
}

