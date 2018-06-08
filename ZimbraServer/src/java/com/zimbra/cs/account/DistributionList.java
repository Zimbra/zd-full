/*
 * 
 */

package com.zimbra.cs.account;

import com.zimbra.common.service.ServiceException;

import java.util.Map;
import java.util.Set;

public class DistributionList extends ZAttrDistributionList implements GroupedEntry {
    
    /*
     * This is for sanity checking purpose.
     * 
     * Certain calls on DistributionList should only be made if the DistributionList
     * object was obtained from prov.getAclGroup, *not* prov.get(DistributionListBy), 
     * *not* prov.searchObjects.   Some calls are vice versa.
     * 
     * mIsAclGroup is true if this object was loaded by prov.getAclGroup.
     */
    boolean mIsAclGroup;
    
    public DistributionList(String name, String id, Map<String, Object> attrs, Provisioning prov) {
        super(name, id, attrs, prov);
    }

    public void modify(Map<String, Object> attrs) throws ServiceException {
        getProvisioning().modifyAttrs(this, attrs);
    }

    public void deleteDistributionList() throws ServiceException {
        getProvisioning().deleteDistributionList(getId());
    }

    public void addAlias(String alias) throws ServiceException {
        getProvisioning().addAlias(this, alias);
    }

    public void removeAlias(String alias) throws ServiceException {
        getProvisioning().removeAlias(this, alias);
    }

    public void renameDistributionList(String newName) throws ServiceException {
        getProvisioning().renameDistributionList(getId(), newName);
    }

    public void addMembers(String[] members) throws ServiceException {
        getProvisioning().addMembers(this, members);
    }

    public void removeMembers(String[] member) throws ServiceException {
        getProvisioning().removeMembers(this, member);
    }

    public String[] getAllMembers() throws ServiceException {
        if (mIsAclGroup)
            throw ServiceException.FAILURE("internal error", null);
        
        return getMultiAttr(Provisioning.A_zimbraMailForwardingAddress);
    }
    
    public Set<String> getAllMembersSet() throws ServiceException {
        if (mIsAclGroup)
            throw ServiceException.FAILURE("internal error", null);
        
        return getMultiAttrSet(Provisioning.A_zimbraMailForwardingAddress);
    }
    
    public String[] getAliases() throws ServiceException {
        if (mIsAclGroup)
            throw ServiceException.FAILURE("internal error", null);
        
        return getMultiAttr(Provisioning.A_zimbraMailAlias);
    }
    
    @Override
    protected void resetData() {
        super.resetData();
        if (mIsAclGroup)
            trimForAclGroup();
    }

    private void trimForAclGroup() {
        /*
         * Hack.
         * 
         * We do not want to cache zimbraMailAlias/zimbraMailForwardingAddress.
         * zimbraMailForwardingAddress can be big.
         * zimbraMailAlias was loaded for computing the upward membership and is now no longer 
         * needed.  Remove it before caching.
         */ 
        Map<String, Object> attrs = getAttrs(false);
        attrs.remove(Provisioning.A_zimbraMailAlias);
        attrs.remove(Provisioning.A_zimbraMailForwardingAddress);
    }
    
    public boolean isAclGroup() {
        return mIsAclGroup;
    }
    
    public void turnToAclGroup() {
        mIsAclGroup = true;
        trimForAclGroup();
    }
    
    public String[] getAllAddrsAsGroupMember() throws ServiceException {
        String aliases[] = getAliases();
        String addrs[] = new String[aliases.length+1];
        addrs[0] = getName();
        for (int i=0; i < aliases.length; i++)
            addrs[i+1] = aliases[i];
        return addrs;
    }

}
