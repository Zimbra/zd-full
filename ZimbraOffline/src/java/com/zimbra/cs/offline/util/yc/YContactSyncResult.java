/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import java.util.Collection;

public interface YContactSyncResult {

    public abstract Collection<Contact> getContacts();
    
    public abstract String getYahooRev();
    
    public abstract boolean isPushResult();
}
