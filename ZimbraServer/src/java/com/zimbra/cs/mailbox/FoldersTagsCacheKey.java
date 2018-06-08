/*
 * 
 */

package com.zimbra.cs.mailbox;

import com.zimbra.common.util.memcached.MemcachedKey;
import com.zimbra.cs.memcached.MemcachedKeyPrefix;

public class FoldersTagsCacheKey implements MemcachedKey {
    private String mKeyStr;

    public FoldersTagsCacheKey(String accountId, int changeToken) {
        mKeyStr = accountId + ":" + changeToken;
    }

    public boolean equals(Object other) {
        if (other instanceof FoldersTagsCacheKey) {
            FoldersTagsCacheKey otherKey = (FoldersTagsCacheKey) other;
            return mKeyStr.equals(otherKey.mKeyStr);
        }
        return false;
    }

    public int hashCode() {
        return mKeyStr.hashCode();
    }

    // MemcachedKey interface
    public String getKeyPrefix() { return MemcachedKeyPrefix.MBOX_FOLDERS_TAGS; }
    public String getKeyValue() { return mKeyStr; }
}
