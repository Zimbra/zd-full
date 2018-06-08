/*
 * 
 */
package com.zimbra.cs.service.mail;

import java.io.IOException;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.util.yauth.MetadataTokenStore;
import com.zimbra.cs.util.yauth.TokenAuthenticateV1;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 */
public class GetYahooCookie extends MailDocumentHandler {
    
    private static final String APPID = "ZYMSGRINTEGRATION";

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        
        MetadataTokenStore mts = new MetadataTokenStore(mbox);
        
        String userId = request.getAttribute("user");
        
        Element response = zsc.createElement(MailConstants.GET_YAHOO_COOKIE_RESPONSE); 
        
        String token = mts.getToken(APPID, userId);
        if (token == null) {
            response.addAttribute("error", "NoToken");
        }
        
        try {
            TokenAuthenticateV1 auth = TokenAuthenticateV1.doAuth(userId, token);
            response.addAttribute("crumb", auth.getCrumb());
            response.addAttribute("Y", auth.getY());
            response.addAttribute("T", auth.getT());
        } catch (IllegalArgumentException ex) {
            response.addAttribute("error", "InvalidToken");
        } catch (IOException e) {
            throw ServiceException.FAILURE("IOException attempting to auth with token", e);
        } 
        return response;
    }
}
