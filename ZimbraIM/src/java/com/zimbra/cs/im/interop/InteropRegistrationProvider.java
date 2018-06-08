/*
 * 
 */
package com.zimbra.cs.im.interop;

import java.io.IOException;
import java.util.Map;

import org.xmpp.packet.JID;

/**
 * This class is a bit of a hack - it allows things in the ZimbraIM project to call down into the IMPersona
 * without creating nasty cross-project dependency issues.
 *   
 * The IMPersona stores the actual gateway registration data for the user.
 */
public interface InteropRegistrationProvider {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    public Map<String, String> getIMGatewayRegistration(JID userJID, String serviceName) throws IOException;
    
    public void putIMGatewayRegistration(JID userJID, String serviceName, Map<String, String> data) throws IOException;
    
    public void removeIMGatewayRegistration(JID userJID, String serviceName) throws IOException;
}
