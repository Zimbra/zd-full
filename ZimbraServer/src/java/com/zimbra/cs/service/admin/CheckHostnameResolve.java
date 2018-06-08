/*
 * 
 */

/*
 * Created on Jun 17, 2004
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.ldap.Check;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author schemers
 */
public class CheckHostnameResolve extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context) throws ServiceException {

        ZimbraSoapContext zsc = getZimbraSoapContext(context);

	    String host = request.getAttribute(AdminConstants.E_HOSTNAME).toLowerCase();

        Check.Result r = Check.checkHostnameResolve(host);

	    Element response = zsc.createElement(AdminConstants.CHECK_HOSTNAME_RESOLVE_RESPONSE);
        response.addElement(AdminConstants.E_CODE).addText(r.getCode());
        String message = r.getMessage();
        if (message != null)
            response.addElement(AdminConstants.E_MESSAGE).addText(message);
	    return response;
	}
	
	@Override
	public void docRights(List<AdminRight> relatedRights, List<String> notes) {
	    notes.add(AdminRightCheckPoint.Notes.ALLOW_ALL_ADMINS);
	}
}