/*
 * 
 */

package com.zimbra.cs.account.callback;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AttributeCallback;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.AttributeManager.IDNType;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.IDNUtil;


public class IDNCallback extends AttributeCallback {

    /**
     * check to make sure zimbraMailHost points to a valid server zimbraServiceHostname
     */
    public void preModify(Map context, String attrName, Object value,
            Map attrsToModify, Entry entry, boolean isCreate) throws ServiceException {
        
        MultiValueMod mod = multiValueMod(attrsToModify, attrName);
        IDNType idnType = AttributeManager.idnType(AttributeManager.getInstance(), attrName);
        
        if (mod.adding() || mod.replacing()) {
            Set<String> asciiValues = new HashSet<String>();
            List<String> addrs = mod.values();
            for (String addr : addrs) {
                if (addr == null || addr.equals("")) continue;
                
                String asciiName;
                if (addr.charAt(0) == '@') {
                    // meant for catchall addresses
                    asciiName = "@" + IDNUtil.toAsciiDomainName(addr.substring(1));
                } else
                    asciiName = IDNUtil.toAscii(addr, idnType);
                
                asciiValues.add(asciiName);
            }
            
            String aName = (mod.adding()?"+":"") + attrName;
            attrsToModify.remove(aName);
            attrsToModify.put(aName, asciiValues.toArray(new String[asciiValues.size()]));
        }
    }

    public void postModify(Map context, String attrName, Entry entry, boolean isCreate) {

    }
}