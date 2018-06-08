/*
 * 
 */
package com.zimbra.cs.account.ldap.upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;


public class ZimbraMessageCacheSize extends LdapUpgrade {

    ZimbraMessageCacheSize() throws ServiceException {
    }
    
    @Override
    void doUpgrade() throws ServiceException {
        ZimbraLdapContext zlc = new ZimbraLdapContext(true);
        try {
            doGlobalConfig(zlc);
            doAllServers(zlc);
        } finally {
            ZimbraLdapContext.closeContext(zlc);
        }

    }
    
    private void doEntry(ZimbraLdapContext zlc, Entry entry, String entryName) throws ServiceException {
        
        String attrName = Provisioning.A_zimbraMessageCacheSize;
        String newValue = "2000";
        
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("Checking " + attrName + " on " + entryName);
        
        String curValue = entry.getAttr(attrName, false);
        if (curValue != null) {
            
            System.out.println("    Setting " + attrName + " on " + entryName + " from " + curValue + " to " + newValue);
            
            Map<String, Object> attr = new HashMap<String, Object>();
            attr.put(Provisioning.A_zimbraMessageCacheSize, newValue);
            mProv.modifyAttrs(entry, attr);
        }
    }

    private void doGlobalConfig(ZimbraLdapContext zlc) throws ServiceException {
        Config config = mProv.getConfig();
        doEntry(zlc, config, "global config");
    }
    
    private void doAllServers(ZimbraLdapContext zlc) throws ServiceException {
        List<Server> servers = mProv.getAllServers();
        
        for (Server server : servers)
            doEntry(zlc, server, "server " + server.getName());
    }

}
