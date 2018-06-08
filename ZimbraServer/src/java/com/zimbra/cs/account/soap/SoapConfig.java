/*
 * 
 */

package com.zimbra.cs.account.soap;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.Element.XMLElement;

class SoapConfig extends Config implements SoapEntry {
    
    SoapConfig(Map<String, Object> attrs, Provisioning provisioning) {
        super(attrs, provisioning);
    }

    SoapConfig(Element e, Provisioning provisioning) throws ServiceException {
        super(SoapProvisioning.getAttrs(e), provisioning);
    }
    
    public void modifyAttrs(SoapProvisioning prov, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        XMLElement req = new XMLElement(AdminConstants.MODIFY_CONFIG_REQUEST);
        SoapProvisioning.addAttrElements(req, attrs);
        setAttrs(SoapProvisioning.getAttrs(prov.invoke(req)));
    }

    public void reload(SoapProvisioning prov) throws ServiceException {
        XMLElement req = new XMLElement(AdminConstants.GET_ALL_CONFIG_REQUEST);
        setAttrs(SoapProvisioning.getAttrs(prov.invoke(req)));
    }
}
