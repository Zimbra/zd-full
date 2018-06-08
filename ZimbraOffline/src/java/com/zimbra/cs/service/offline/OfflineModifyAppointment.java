/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.service.mail.ModifyAppointment;

public class OfflineModifyAppointment extends ModifyAppointment {
    
    @Override
    protected String getProxyAuthToken(String requestedAccountId, Map<String, Object> context) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        return prov.getCalendarProxyAuthToken(requestedAccountId, context);
    }

    @Override
    public void preProxy(Element request, Map<String, Object> context) throws ServiceException {        
        OfflineProxyHelper.uploadAttachments(request, getZimbraSoapContext(context).getRequestedAccountId());
    }
}
