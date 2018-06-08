/*
 * 
 */

package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.account.accesscontrol.AdminRight;

public class GetLicenseInfo extends AdminDocumentHandler {

    static final String TRIAL_EXPIRATION_DATE_KEY = "trial_expiration_date";

    public Element handle(Element request, Map<String, Object> context) {
        ZimbraSoapContext lc = getZimbraSoapContext(context);

        String expirationDate = LC.get(TRIAL_EXPIRATION_DATE_KEY);
        Element response = lc.createElement(AdminConstants.GET_LICENSE_INFO_RESPONSE);
        Element el = response.addElement(AdminConstants.E_LICENSE_EXPIRATION);
        el.addAttribute(AdminConstants.A_LICENSE_EXPIRATION_DATE, expirationDate);
        return response;
    }

    public boolean needsAdminAuth(Map<String, Object> context) {
        return false;
    }

    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }

    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        notes.add(AdminRightCheckPoint.Notes.ALLOW_ALL_ADMINS);
    }
}
