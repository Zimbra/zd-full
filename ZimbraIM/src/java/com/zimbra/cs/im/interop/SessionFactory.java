/*
 * 
 */
package com.zimbra.cs.im.interop;

import org.xmpp.packet.JID;

import com.zimbra.common.service.ServiceException;

public interface SessionFactory {
    /**
     * Encode the password (possibly by communicating with the IM service and grabbing a "token") 
     * before it is stored in the Zimbra DB.  The value returned by this function will be stored
     * in the User's DB and it will be passed to createSession().
     * 
     * @param service
     * @param jid
     * @param username
     * @param password
     * @return
     */
    String encodePassword(Service service, JID jid, String username, String password)  throws ServiceException;
    
    InteropSession createSession(Service service, JID jid, String username, String password);
    boolean isEnabled();
    
    String getName();
    String getDescription();
}