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
import com.zimbra.cs.im.IMChat.MucStatusCode;
import com.zimbra.cs.service.im.IMDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class IMJoinConferenceRoom extends IMDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(IMConstants.IM_JOIN_CONFERENCE_ROOM_RESPONSE);
        
        String threadId = request.getAttribute(IMConstants.A_THREAD_ID, null);
        String addr = request.getAttribute(IMConstants.A_ADDRESS);
        String nickname = request.getAttribute("nick", null);
        String password = request.getAttribute("password", null);
        
        IMPersona persona = super.getRequestedPersona(zsc);
        Pair<String, List<MucStatusCode>> results = persona.joinConferenceRoom(addr, threadId, nickname, password);
        StringBuilder errors = new StringBuilder();
        StringBuilder status = new StringBuilder();
        for (MucStatusCode code : results.getSecond()) {
            if (code.isError()) {
                if (errors.length() > 0)
                    errors.append(",");
                errors.append(code.name());
            } else {
                if (status.length() > 0)
                    status.append(",");
                status.append(code.name());
            }
        }
        response.addAttribute(IMConstants.A_THREAD_ID, results.getFirst());
        if (status.length() > 0)
            response.addAttribute("status", status.toString());
        if (errors.length() > 0)
            response.addAttribute("error", errors.toString()); 
        return response;
    }

}
