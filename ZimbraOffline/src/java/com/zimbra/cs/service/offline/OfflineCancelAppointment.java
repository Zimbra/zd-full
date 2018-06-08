/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.service.mail.CancelAppointment;


public class OfflineCancelAppointment extends CancelAppointment {
    @Override
    protected String getProxyAuthToken(String requestedAccountId, Map<String, Object> context) throws ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        return prov.getCalendarProxyAuthToken(requestedAccountId, context);
    }
}
