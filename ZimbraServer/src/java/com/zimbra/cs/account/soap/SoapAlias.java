/*
 * 
 */

package com.zimbra.cs.account.soap;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Alias;
import com.zimbra.cs.account.Provisioning;

class SoapAlias extends Alias implements SoapEntry {

    SoapAlias(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, prov);
    }

    SoapAlias(Element e, Provisioning prov) throws ServiceException {
        super(e.getAttribute(AdminConstants.A_NAME), e.getAttribute(AdminConstants.A_ID), SoapProvisioning.getAttrs(e), prov);
    }

    public void modifyAttrs(SoapProvisioning prov, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException {
        throw new UnsupportedOperationException();
    }

    public void reload(SoapProvisioning prov) throws ServiceException {

    }
}
