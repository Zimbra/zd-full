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
public class IMListConferenceRooms extends IMDocumentHandler {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_LIST_CONFERENCE_ROOMS_RESPONSE);
        IMPersona persona = super.getRequestedPersona(zsc);
        
        String svc = request.getAttribute("svc");
        List<Pair<String/*name*/, String/*JID*/>> rooms = persona.listRooms(svc);
        for (Pair<String/*name*/, String/*JID*/> pair : rooms) {
            Element elt = response.addElement("room");
            elt.addAttribute("name", pair.getFirst());
            elt.addAttribute("addr", pair.getSecond());
        }
        return response;
    }
}
