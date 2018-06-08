/*
 * 
 */

package com.zimbra.common.util.memcached;

import com.zimbra.common.service.ServiceException;

/**
 * Serializes an object of type V to String, and deserializes a String to a V object.
 *
 * @param <V>
 */
public interface MemcachedSerializer<V> {

    public Object serialize(V value) throws ServiceException;
    public V deserialize(Object obj) throws ServiceException;
}
