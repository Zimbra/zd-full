/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMChat;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.IMChat.Participant;
import com.zimbra.cs.session.Session;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class IMGetRoster extends IMDocumentHandler {

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_GET_ROSTER_RESPONSE);
        
        IMPersona persona = super.getRequestedPersona(zsc);
        synchronized (persona.getLock()) {
            Element pres = response.addUniqueElement(IMConstants.E_PRESENCE);
            persona.getEffectivePresence().toXml(pres);
            
            // chats
            Element chats = response.addUniqueElement(IMConstants.E_CHATS);
            for (IMChat chat : persona.chats()) {
                Element e = chats.addElement(IMConstants.E_CHATS);
                e.addAttribute(IMConstants.A_THREAD_ID, chat.getThreadId());
                
                Element participantsElt = e.addElement(IMConstants.E_PARTICIPANTS);
                for (Participant part : chat.participants()) {
                    Element pe = participantsElt.addElement(IMConstants.E_PARTICIPANT);
                    pe.addAttribute(IMConstants.A_ADDRESS, part.getAddress().getAddr());
                }
            }
            Session s = this.getSession(zsc);
            if (s != null) {
                persona.refreshRoster(s);
                persona.getDefaultPrivacyList();
                persona.refreshChats(s);
            }
        }

        return response;
    }
}
