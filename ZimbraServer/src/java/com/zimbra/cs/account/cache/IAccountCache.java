/*
 * 
 */
package com.zimbra.cs.account.cache;

import com.zimbra.cs.account.Account;

public interface IAccountCache extends IEntryCache {
    public void clear();
    public void remove(Account entry);
    public void put(Account entry);
    public void replace(Account entry);
    public Account getById(String key);
    public Account getByName(String key);
    public Account getByForeignPrincipal(String key);
}
