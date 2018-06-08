/*
 * 
 */
package org.jivesoftware.util;

/**
 * Thunk to handle ZimbraIM--ZimbraServer split
 * 
 * Returns config values that are only accessible from ZimbraServer
 */
public class LdapConfig {
    static IMConfigProperty getLdapProp(String name) {
        return sProvider.getLdapProp(name);
    }
    
    public interface ServerConfigProvider {
        // Provisioning.java
        IMConfigProperty getLdapProp(String name);
        
        // Providers Implemented 
        IMConfigProperty getConnectionProvider();
        IMConfigProperty getUserProvider();
        IMConfigProperty getAuthProvider();
        IMConfigProperty getGroupProvider();
        IMConfigProperty getProxyTransferProvider();
        IMConfigProperty getRoutingTableProvider();
        IMConfigProperty getVCardProvider();
    }
    
    private static ServerConfigProvider sProvider;
    
    
    public static ServerConfigProvider getProvider() { return sProvider; }
    
    public static void setProvider(ServerConfigProvider prov) {
        sProvider = prov;
    }
  
}
