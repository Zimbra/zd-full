/*
 * 
 */
package com.zimbra.cs.session;

import java.util.List;

/**
 * User-supplied callback which is set by doWait() and which is called when one 
 * or more of the waiting sessions has new data.
 */
public interface WaitSetCallback {
    void dataReady(IWaitSet ws, String seqNo, boolean cancelled, List<WaitSetError> errors, String[] signalledAccounts);
}