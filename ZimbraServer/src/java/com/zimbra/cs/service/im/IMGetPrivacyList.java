/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class IMGetPrivacyList extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        String name = request.getAttribute(IMConstants.A_NAME, null);

        IMPersona persona = getRequestedPersona(zsc);
        synchronized (persona.getLock()) {
            if (name == null) 
                persona.getDefaultPrivacyList();
            else
                persona.requestPrivacyList(name);
        }
            
        return zsc.createElement(IMConstants.IM_GET_PRIVACY_LIST_RESPONSE);
    }

}
