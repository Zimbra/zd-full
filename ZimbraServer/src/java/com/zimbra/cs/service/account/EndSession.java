/*
 * 
 */
package com.zimbra.cs.service.account;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.session.Session;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * End the current session immediately cleaning up all resources used by the session
 * including the notification buffer and logging the session out from IM if applicable
 */
public class EndSession extends AccountDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context)
    throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        if (zsc.hasSession()) {
            Session s = getSession(zsc);
            endSession(s);
        }
        Element response = zsc.createElement(AccountConstants.END_SESSION_RESPONSE); 
        return response;
    }
}
