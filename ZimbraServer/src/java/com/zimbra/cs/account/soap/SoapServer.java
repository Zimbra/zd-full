/*
 * 
 */

package com.zimbra.cs.account.soap;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.common.soap.Element.XMLElement;

class SoapServer extends Server implements SoapEntry {

    SoapServer(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, null, prov);
    }

    SoapServer(Element e, Provisioning prov) throws ServiceException {
        super(e.getAttribute(AdminConstants.A_NAME), e.getAttribute(AdminConstants.A_ID), SoapProvisioning.getAttrs(e), null, prov);
    }

    public void modifyAttrs(SoapProvisioning prov, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        XMLElement req = new XMLElement(AdminConstants.MODIFY_SERVER_REQUEST);
        req.addElement(AdminConstants.E_ID).setText(getId());
        SoapProvisioning.addAttrElements(req, attrs);
        setAttrs(SoapProvisioning.getAttrs(prov.invoke(req).getElement(AdminConstants.E_SERVER)));
    }

    public void reload(SoapProvisioning prov) throws ServiceException {
        XMLElement req = new XMLElement(AdminConstants.GET_SERVER_REQUEST);
        Element a = req.addElement(AdminConstants.E_SERVER);
        a.setText(getId());
        a.addAttribute(AdminConstants.A_BY, ServerBy.id.name());
        setAttrs(SoapProvisioning.getAttrs(prov.invoke(req).getElement(AdminConstants.E_SERVER)));
    }
}
