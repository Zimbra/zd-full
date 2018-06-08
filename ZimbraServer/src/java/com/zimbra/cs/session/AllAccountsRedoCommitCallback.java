/*
 * 
 */
package com.zimbra.cs.session;

import com.zimbra.cs.redolog.CommitId;
import com.zimbra.cs.redolog.RedoCommitCallback;

/**
 * 
 */
public class AllAccountsRedoCommitCallback implements RedoCommitCallback {
    
    private AllAccountsRedoCommitCallback(String accountId, int changeMask) {
        mAccountId = accountId;
        mChangeMask = changeMask;
    }

    /* @see com.zimbra.cs.redolog.RedoCommitCallback#callback(com.zimbra.cs.redolog.CommitId) */
    public void callback(CommitId cid) {
        AllAccountsWaitSet.mailboxChangeCommitted(cid.encodeToString(), mAccountId, mChangeMask);
    }
    
    public static final AllAccountsRedoCommitCallback getRedoCallbackIfNecessary(String accountId, int changeMask) {
        if (AllAccountsWaitSet.isCallbackNecessary(changeMask)) {
            return new AllAccountsRedoCommitCallback(accountId, changeMask);
        }
        return null;
    }
    
    private final String mAccountId;
    private final int mChangeMask;
}
