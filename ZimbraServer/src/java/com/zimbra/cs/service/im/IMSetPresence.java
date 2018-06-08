/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.IMPresence;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.soap.ZimbraSoapContext;

public class IMSetPresence extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        OperationContext octxt = getOperationContext(zsc, context);

        Element response = zsc.createElement(IMConstants.IM_SET_PRESENCE_RESPONSE);

        Element e = request.getElement(IMConstants.E_PRESENCE);

        String showStr = e.getAttribute(IMConstants.A_SHOW, IMPresence.Show.ONLINE.toString());
        String statusStr = null;
        statusStr = e.getAttribute(IMConstants.A_STATUS, null);

        IMPresence presence = new IMPresence(IMPresence.Show.valueOf(showStr.toUpperCase()), (byte)0, statusStr);

        IMPersona persona = super.getRequestedPersona(zsc);
        synchronized (persona.getLock()) {
            persona.setMyPresence(octxt, presence);
        }

        return response;
    }
}
