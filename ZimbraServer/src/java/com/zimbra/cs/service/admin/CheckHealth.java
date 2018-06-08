/*
 * 
 */

/*
 * Created on 2005. 1. 26.
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.db.DbStatus;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author jhahm
 */
public class CheckHealth extends AdminDocumentHandler {

    /* (non-Javadoc)
     * @see com.zimbra.soap.DocumentHandler#handle(org.dom4j.Element, java.util.Map)
     */
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Element response = lc.createElement(AdminConstants.CHECK_HEALTH_RESPONSE);

        if (needsAdminAuth(context)) {
            Server localServer = Provisioning.getInstance().getLocalServer();
            checkRight(lc, context, localServer, Admin.R_checkHealth);
        }
        
        boolean dir = Provisioning.getInstance().healthCheck();
        boolean db = DbStatus.healthCheck();
        boolean healthy = dir && db;

        response.addAttribute(AdminConstants.A_HEALTHY, healthy);
        return response;
    }

    public boolean needsAuth(Map<String, Object> context) {
        // Must return false to leave the auth decision entirely up to
        // needsAdminAuth().
    	return false;
    }

    /**
     * No auth required if client is localhost.  Otherwise, admin auth is
     * required.
     * @param context
     * @return
     */
    public boolean needsAdminAuth(Map<String, Object> context) {
        return !clientIsLocal(context);
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_checkHealth);
        notes.add("The " + Admin.R_checkHealth.getName() + " is needed " +
                "only when the client making the SOAP request is localhost.");
    }
}
