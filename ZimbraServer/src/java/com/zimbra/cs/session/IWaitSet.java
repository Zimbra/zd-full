/*
 * 
 */

package com.zimbra.cs.session;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;

/**
 * WaitSet: scalable mechanism for listening for changes to one or many accounts */
public interface IWaitSet {

    /**
     * WaitMultipleAccounts:  optionally modifies the wait set and checks
     * for any notifications.  If block=1 and there are no notificatins, then
     * this API will BLOCK until there is data.
     *
     * Client should always set 'seq' to be the highest known value it has
     * received from the server.  The server will use this information to
     * retransmit lost data.
     *
     * If the client sends a last known sync token then the notification is
     * calculated by comparing the accounts current token with the client's
     * last known.
     *
     * If the client does not send a last known sync token, then notification
     * is based on change since last Wait (or change since <add> if this
     * is the first time Wait has been called with the account)
     * 
     * IMPORTANT NOTE: Caller *must* call doneWaiting() when done waiting for the callback 
     * 
     * @param cb
     * @param lastKnownSeqNo
     * @param block
     * @param addAccounts
     * @param updateAccounts
     * @param removeAccounts
     * @return
     * @throws ServiceException
     */
    public List<WaitSetError> doWait(WaitSetCallback cb, String lastKnownSeqNo, 
        List<WaitSetAccount> addAccounts, List<WaitSetAccount> updateAccounts) 
        throws ServiceException;
    
    /**
     * Handle removes separately from the main doWait API -- this is because removes 
     * must be run without holding the WS lock (due to deadlock issues)
     * 
     * @return
     */
    public List<WaitSetError> removeAccounts(List<String> removeAccounts);
    
    public void doneWaiting();
        
    /**
     * Just a helper: the 'default interest' is set when the WaitSet is created,
     * and subsequent requests can access it when creating/updating WaitSetAccounts
     * if the client didn't specify one with the update.
     */
    public int getDefaultInterest();
    
    /**
     * @return The accountID of the owner/creator
     */
    public String getOwnerAccountId();
    
    /**
     * @return the id of this wait set
     */
    public String getWaitSetId();
    
    /**
     * Handle a QueryWaitSet request by encoding all of our internal data
     * into the response
     * 
     * @param response
     */
    public void handleQuery(Element response);
}
