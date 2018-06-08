/*
 * 
 */
package com.zimbra.cs.service.account;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.MailMode;
import com.zimbra.cs.account.Server;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.SoapServlet;


public abstract class AccountDocumentHandler extends DocumentHandler {

    @Override
    protected Element proxyIfNecessary(Element request, Map<String, Object> context) throws ServiceException {
        try {
            // by default, try to execute on the appropriate host
            return super.proxyIfNecessary(request, context);
        } catch (ServiceException e) {
            // if something went wrong proxying the request, just execute it locally
            if (ServiceException.PROXY_ERROR.equals(e.getCode()))
                return null;
            // but if it's a real error, it's a real error
            throw e;
        }
    }
    
    /*
     * bug 27389
     */
    protected boolean checkPasswordSecurity(Map<String, Object> context) throws ServiceException {
        HttpServletRequest req = (HttpServletRequest)context.get(SoapServlet.SERVLET_REQUEST);
        boolean isHttps = req.getScheme().equals("https");
        if (isHttps)
            return true;
        
        // clear text
        Server server = Provisioning.getInstance().getLocalServer();
        String modeString = server.getAttr(Provisioning.A_zimbraMailMode, null);
        if (modeString == null) {
            // not likely, but just log and let it through
            ZimbraLog.soap.warn("missing " + Provisioning.A_zimbraMailMode + 
                                " for checking password security, allowing the request");
            return true;
        }
            
        MailMode mailMode = Provisioning.MailMode.fromString(modeString);
        if (mailMode == MailMode.mixed && 
            !server.getBooleanAttr(Provisioning.A_zimbraMailClearTextPasswordEnabled, true)) 
            return false;
        else
            return true;
    }
}
