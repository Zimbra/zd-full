/*
 * 
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.service.mail.CreateWaitSet;
import com.zimbra.soap.ZimbraSoapContext;

public class AdminCreateWaitSetRequest extends AdminDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(AdminConstants.ADMIN_CREATE_WAIT_SET_RESPONSE);
        return CreateWaitSet.staticHandle(this, request, context, response);
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        notes.add("If allAccounts is specified, " + AdminRightCheckPoint.Notes.SYSTEM_ADMINS_ONLY);
        notes.add("Otherwise, for each requested account, " + AdminRightCheckPoint.Notes.ADMIN_LOGIN_AS);
    }

}
