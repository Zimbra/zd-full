/*
 * 
 */

package com.zimbra.common.util.memcached;

/**
 * Memcached key that supports optional prefix
 */
public interface MemcachedKey {

    /**
     * Returns the memcached key prefix.  Can be null.
     * @return
     */
    public String getKeyPrefix();

    /**
     * Returns the memcached key value, without prefix.
     * @return
     */
    public String getKeyValue();
}
