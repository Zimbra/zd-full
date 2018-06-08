/*
 * 
 */

/*
 * Created on Jun 17, 2004
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;

/**
 * @author schemers
 */
public class Ping extends AdminDocumentHandler {

    /* (non-Javadoc)
      * @see com.zimbra.soap.DocumentHandler#handle(org.dom4j.Element, java.util.Map)
      */
    public Element handle(Element request, Map<String, Object> context) {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Element response = lc.createElement(AdminConstants.PING_RESPONSE);
        return response;
    }

    public boolean needsAuth(Map<String, Object> context) {
        // return false because this may be called from Perl which
        // doesn't have auth token
        return false;
    }

    public boolean needsAdminAuth(Map<String, Object> context) {
        // return false because this may be called from Perl which
        // doesn't have auth token
        return false;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        notes.add(AdminRightCheckPoint.Notes.ALLOW_ALL_ADMINS);
    }
}
