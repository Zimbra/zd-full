/*
 * 
 */
package com.zimbra.cs.session;

import com.zimbra.common.service.ServiceException;
import com.zimbra.soap.SoapSessionFactory;

public class OfflineSoapSessionFactory extends SoapSessionFactory {
    @Override
    public SoapSession getSoapSession(String authAccountId, boolean isLocal, boolean asAdmin) throws ServiceException {
        if (isLocal) {
            return new OfflineSoapSession(authAccountId, asAdmin);
        } else {
            return new OfflineRemoteSoapSession(authAccountId, asAdmin);
        }
    }
}
