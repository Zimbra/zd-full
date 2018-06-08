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

import java.util.HashMap;
import java.util.Map;

/**
 * @author schemers
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Config extends ZAttrConfig {
    
    private Map<String, Object> mDomainDefaults = new HashMap<String, Object>();
    private Map<String, Object> mServerDefaults = new HashMap<String, Object>();    

    public Config(Map<String, Object> attrs, Provisioning provisioning) {
        super(attrs, provisioning);
        resetData();
    }
    
    public String getLabel() {
        return "globalconfig";
    }

    public void modify(Map<String, Object> attrs) throws ServiceException {
        getProvisioning().modifyAttrs(this, attrs);
    }

    @Override
    public void resetData() {
        super.resetData();
        try {
            getDefaults(AttributeFlag.domainInherited, mDomainDefaults);
            getDefaults(AttributeFlag.serverInherited, mServerDefaults);            
        } catch (ServiceException e) {
            // TODO log?
        }
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDomainDefaults() {
        return mDomainDefaults;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getServerDefaults() {
        return mServerDefaults;
    }

}
