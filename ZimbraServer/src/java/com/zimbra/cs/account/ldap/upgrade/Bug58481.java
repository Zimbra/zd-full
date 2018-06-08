/*
 * 
 */
package com.zimbra.cs.account.ldap.upgrade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.accesscontrol.TargetType;

public class Bug58481 extends LdapUpgrade {
    Bug58481() throws ServiceException {
    }
    
    @Override
    void doUpgrade() throws ServiceException {
        upgradeZimbraGalLdapAttrMap();
    }
    
    private void upgradeZimbraGalLdapAttrMap() throws ServiceException {
        final String attrName = Provisioning.A_zimbraGalLdapAttrMap;
        
        final String[] valuesToAdd = new String[] {
            "objectClass=objectClass",
            "zimbraId=zimbraId",
            "zimbraMailForwardingAddress=member"
        };
        
        Config config = mProv.getConfig();
        
        Map<String, Object> attrs = new HashMap<String, Object>();
        
        Set<String> curValues = config.getMultiAttrSet(attrName);
        
        for (String valueToAdd : valuesToAdd) {
            if (!curValues.contains(valueToAdd)) {
                StringUtil.addToMultiMap(attrs, "+" + attrName, valueToAdd);
            }
        }
        
        modifyAttrs(config, attrs);
    }
    
    
}
