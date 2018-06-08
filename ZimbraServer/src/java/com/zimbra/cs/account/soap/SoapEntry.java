/*
 * 
 */

package com.zimbra.cs.account.soap;

import java.util.Map;

import com.zimbra.common.service.ServiceException;

interface SoapEntry {
    
    public void modifyAttrs(SoapProvisioning prov, Map<String, ? extends Object> attrs, boolean checkImmutable) throws ServiceException;
    public void reload(SoapProvisioning prov) throws ServiceException;

}
