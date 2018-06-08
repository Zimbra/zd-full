/*
 * 
 */
package com.zimbra.cs.service.admin;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.XMPPComponent;
import com.zimbra.cs.account.Provisioning.XMPPComponentBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * <DeleteXMPPComponentRequest>
 *    <xmppcomponent by="by">identifier</xmppcomponent>
 * </DeleteXMPPComponentRequest>   
 */
public class DeleteXMPPComponent extends AdminDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
        
        Element id = request.getElement(AdminConstants.E_XMPP_COMPONENT);
        String byStr = id.getAttribute(AdminConstants.A_BY);
        String name = id.getText();
        
        if (name == null || name.equals(""))
            throw ServiceException.INVALID_REQUEST("must specify a value for a xmppcomponent", null);
        
        XMPPComponentBy by = Provisioning.XMPPComponentBy.valueOf(byStr);
        
        XMPPComponent comp = prov.get(by, name);
        if (comp == null) {
            throw AccountServiceException.NO_SUCH_XMPP_COMPONENT(name);
        }
        
        checkRight(zsc, context, comp, Admin.R_deleteXMPPComponent);
        
        prov.deleteXMPPComponent(comp);
        
        Element response = zsc.createElement(AdminConstants.DELETE_XMPPCOMPONENT_RESPONSE);
        return response;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_deleteXMPPComponent);
    }
}
