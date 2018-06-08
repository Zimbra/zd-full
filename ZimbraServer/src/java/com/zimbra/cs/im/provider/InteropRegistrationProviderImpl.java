/*
 * 
 */
package com.zimbra.cs.im.provider;

import java.io.IOException;
import java.util.Map;

import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.cs.im.IMAddr;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.cs.im.IMRouter;
import com.zimbra.cs.im.interop.InteropRegistrationProvider;

public class InteropRegistrationProviderImpl implements InteropRegistrationProvider {

    public Map<String, String> getIMGatewayRegistration(JID userJID, String serviceName) throws IOException {
        try {
            IMPersona persona = IMRouter.getInstance().findPersona(null, IMAddr.fromJID(userJID));
            return persona.getIMGatewayRegistration(serviceName);
        } catch (ServiceException e) {
            throw new IOException("Caught IOException when trying to fetch persona: "+e.toString()+" "+SystemUtil.getStackTrace(e));
        }
    }

    public void putIMGatewayRegistration(JID userJID, String serviceName, Map<String, String> data) throws IOException {
        try {
            IMPersona persona = IMRouter.getInstance().findPersona(null, IMAddr.fromJID(userJID));
            persona.setIMGatewayRegistration(serviceName, data);
        } catch (ServiceException e) {
            throw new IOException("Caught IOException when trying to fetch persona: "+e.toString()+" "+SystemUtil.getStackTrace(e));
        }
    }
    
    public void removeIMGatewayRegistration(JID userJID, String serviceName) throws IOException {
        try {
            IMPersona persona = IMRouter.getInstance().findPersona(null, IMAddr.fromJID(userJID));
            persona.removeIMGatewayRegistration(serviceName);
        } catch (ServiceException e) {
            throw new IOException("Caught IOException when trying to fetch persona: "+e.toString()+" "+SystemUtil.getStackTrace(e));
        }
    }

}
