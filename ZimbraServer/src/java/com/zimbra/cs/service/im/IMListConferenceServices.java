/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class IMListConferenceServices extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_LIST_CONFERENCE_SERVICES_RESPONSE);
        IMPersona persona = super.getRequestedPersona(zsc);
        List<Pair<String /*name*/, String /*JID*/>> l = persona.listConferenceServices();
        
        for (Pair<String,String> p : l) {
            Element svc = response.addElement("svc");
            svc.addAttribute("name", p.getFirst());
            svc.addAttribute("addr", p.getSecond());
        }
        return response;
    }
}
