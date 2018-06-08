/*
 * 
 */

/*
 * Created on Sep 23, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.account;

import com.zimbra.common.service.ServiceException;

import java.util.Map;

/**
 * @author schemers
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Server extends ZAttrServer {
    
    public Server(String name, String id, Map<String,Object> attrs, Map<String,Object> defaults, Provisioning prov) {
        super(name, id, attrs, defaults, prov);
    }

    public void deleteServer(String zimbraId) throws ServiceException {
        getProvisioning().deleteServer(getId());
    }

    public void modify(Map<String, Object> attrs) throws ServiceException {
        getProvisioning().modifyAttrs(this, attrs);
    }
    
    /*
     * compare only proto and host, ignore port, because if port on the server was changed we 
     * still want the change to go through.
     */
    public boolean mailTransportMatches(String mailTransport) {
        // if there is no mailTransport, it sure "matches"
        if (mailTransport == null)
            return true;
        
        String serviceName = getAttr(Provisioning.A_zimbraServiceHostname, null);
        
        String[] parts = mailTransport.split(":");
        if (serviceName != null && parts.length == 3) {
            if (parts[0].equalsIgnoreCase("lmtp") && parts[1].equals(serviceName))
                return true;
        }
        
        return false;
    }

}
