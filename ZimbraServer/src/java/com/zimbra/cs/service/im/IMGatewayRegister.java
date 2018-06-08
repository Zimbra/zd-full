/*
 * 
 */
package com.zimbra.cs.service.im;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.IMConstants;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class IMGatewayRegister extends IMDocumentHandler 
{
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        
        Element response = zsc.createElement(IMConstants.IM_GATEWAY_REGISTER_RESPONSE);
        IMPersona persona = super.getRequestedPersona(zsc);
        
        String op = request.getAttribute("op");
        String serviceStr = request.getAttribute("service");
        boolean result = true;
        if ("reg".equals(op)) {
            String nameStr = request.getAttribute("name");
            String pwStr = request.getAttribute("password");
            persona.gatewayRegister(serviceStr, nameStr, pwStr);
        } else if ("reconnect".equals(op)) {
            persona.gatewayReconnect(serviceStr);
        } else {
            persona.gatewayUnRegister(serviceStr);
        }
        response.addAttribute("result", result);
        
        return response;
    }
}
