/*
 * 
 */
/*
 * Created on Aug 31, 2005
 */
package com.zimbra.cs.session;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.EntrySearchFilter;
import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapEntrySearchFilter;

import java.util.HashMap;
import java.util.List;

/** @author dkarp */
public class AdminSession extends Session {

    private static final long ADMIN_SESSION_TIMEOUT_MSEC = 10 * Constants.MILLIS_PER_MINUTE;
  
    private AccountSearchParams mSearchParams;
    private HashMap<String,Object> mData = new HashMap<String,Object>();

    public AdminSession(String accountId) {
        super(accountId, Session.Type.ADMIN);
    }

    @Override
    protected boolean isMailboxListener() {
        return false;
    }

    @Override
    protected boolean isRegisteredInCache() {
        return true;
    }

    @Override
    protected long getSessionIdleLifetime() {
        return ADMIN_SESSION_TIMEOUT_MSEC;
    }
    
    public Object getData(String key) { return mData.get(key); }
    public void setData(String key, Object data) { mData.put(key, data); }
    public void clearData(String key) { mData.remove(key); }

    @Override public void notifyPendingChanges(PendingModifications pns, int changeId, Session source) { }

    @Override protected void cleanup() { }

    public List searchAccounts(Domain d, String query, String[] attrs, String sortBy,
            boolean sortAscending, int flags, int offset, int maxResults,
            NamedEntry.CheckRight rightChecker) throws ServiceException {
        AccountSearchParams params = new AccountSearchParams(d, query, attrs, sortBy, sortAscending, flags, maxResults, rightChecker);
        boolean needToSearch =  (offset == 0) || (mSearchParams == null) || !mSearchParams.equals(params);
        //ZimbraLog.account.info("this="+this+" mSearchParams="+mSearchParams+" equal="+!params.equals(mSearchParams));
        if (needToSearch) {
            //ZimbraLog.account.info("doing new search: "+query+ " offset="+offset);
            params.doSearch();
            mSearchParams = params;
        } else {
            //ZimbraLog.account.info("cached search: "+query+ " offset="+offset);
        }
        return mSearchParams.mResult;
    }

    public List searchCalendarResources(
            Domain d, EntrySearchFilter filter, String[] attrs, String sortBy,
            boolean sortAscending, int offset, NamedEntry.CheckRight rightChecker)
    throws ServiceException {
        String query = LdapEntrySearchFilter.toLdapCalendarResourcesFilter(filter);
        return searchAccounts(
                d, query, attrs, sortBy, sortAscending,
                Provisioning.SA_CALENDAR_RESOURCE_FLAG,
                offset, 0, rightChecker);
    }
}
