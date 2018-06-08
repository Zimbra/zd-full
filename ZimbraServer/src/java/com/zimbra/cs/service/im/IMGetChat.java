/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;

import com.zimbra.cs.im.IMChat;
import com.zimbra.cs.im.IMMessage;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.IMChat.Participant;
import com.zimbra.cs.im.IMMessage.Lang;
import com.zimbra.cs.im.IMMessage.TextPart;
import com.zimbra.soap.ZimbraSoapContext;

public class IMGetChat extends IMDocumentHandler {
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_GET_CHAT_RESPONSE);
        
        String threadId = request.getAttribute(IMConstants.A_THREAD_ID);
        
        IMPersona persona = super.getRequestedPersona(zsc);
        synchronized (persona.getLock()) {
            IMChat chat = persona.getChat(threadId);
            if (chat != null)
                response = chatToXml(chat, response);
            else
                response.addAttribute(IMConstants.A_ERROR, "not_found");
        }
        
        response.addAttribute(IMConstants.A_THREAD_ID, threadId);
        
        return response;        
    }
    
    public static Element chatToXml(IMChat chat, Element parent) {
        // chat
        Element ce = parent.addElement(IMConstants.E_CHAT);
        ce.addAttribute(IMConstants.A_THREAD_ID, chat.getThreadId());
        
        // participants
        {
            Element e = ce.addElement(IMConstants.E_PARTICIPANTS);
            for (Participant part : chat.participants()) {
                part.toXML(e);
            }
        }
        
        // messages
        {
            Element messages = ce.addElement(IMConstants.E_MESSAGES);
            int curOffset = 0;
            
            for (IMMessage msg : chat.messages()) {
                Element me = messages.addElement(IMConstants.E_MESSAGE);
                me.addAttribute(IMConstants.A_SEQ, curOffset+chat.getFirstSeqNo());
                me.addAttribute(IMConstants.A_TIMESTAMP, msg.getTimestamp());
                me.addAttribute(IMConstants.A_FROM, msg.getFrom().getAddr());
                
                // subject 
                {
                    TextPart subj = msg.getSubject(Lang.DEFAULT);
                    if (subj != null) {
                        Element se = me.addElement(IMConstants.E_SUBJECT);
                        se.setText(subj.getPlainText());
                    }
                }
                
                // body
                {
                    TextPart body = msg.getBody(Lang.DEFAULT);
                    if (body != null) {
                        String s = body.toString();
                        me.addElement("body").setText(s);
                    }
                }
                
                curOffset++;
            }
            return parent;
        }
    }
    
}
