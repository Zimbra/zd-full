/*
 * 
 */
package com.zimbra.cs.offline.ab.gab;

import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.common.service.ServiceException;
import com.google.gdata.data.BaseEntry;

import java.net.URL;
import java.net.MalformedURLException;

public final class Gab {
    public static final String BASE_URL = OfflineLC.zdesktop_gab_base_url.value();

    public static final String APP_NAME = String.format("Zimbra-%s-%s",
        OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());

    public static final String CONTACTS = "/contacts/";
    public static final String GROUPS = "/groups/";


    public static boolean isContactId(String id) {
        return id != null && id.contains(CONTACTS);
    }

    public static boolean isGroupId(String id) {
        return id != null && id.contains(GROUPS);
    }

    public static URL getEditUrl(BaseEntry entry) throws MalformedURLException {
        return new URL(entry.getEditLink().getHref());
    }

    public static URL toUrl(String url) throws ServiceException {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw ServiceException.FAILURE("Bad URL format: " + url, null);
        }
    }
}
