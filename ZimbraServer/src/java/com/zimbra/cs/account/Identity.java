/*
 * 
 */

package com.zimbra.cs.account;

import java.util.Map;
import com.zimbra.common.service.ServiceException;

/**
 * @author schemers
 */
public class Identity extends AccountProperty implements Comparable {

    public Identity(Account acct, String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(acct, name, id, attrs, null, prov);
    }
    
    /**
     * this should only be used internally by the server. it doesn't modify the real id, just
     * the cached one.
     * @param id
     */
    public void setId(String id) {
        mId = id;
        getRawAttrs().put(Provisioning.A_zimbraPrefIdentityId, id);
    }
}


