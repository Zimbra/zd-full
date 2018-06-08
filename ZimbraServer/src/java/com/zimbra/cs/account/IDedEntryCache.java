/*
 * 
 */
package com.zimbra.cs.account;

import java.util.List;
import java.util.Map;

import com.zimbra.common.util.MapUtil;

/**
 * 
 * @author pshao
 *
 */
public class IDedEntryCache<E extends NamedEntry> {
    
    private Map mIdCache;
    
    private long mRefreshTTL;

    static class CacheEntry<E extends NamedEntry> {
        long mLifetime;
        E mEntry;
        CacheEntry(E entry, long expires) {
            mEntry = entry;
            mLifetime = System.currentTimeMillis() + expires;
        }
        
        boolean isStale() {
            return mLifetime < System.currentTimeMillis();
        }
    }
    
    /**
     * @param maxItems
     * @param refreshTTL
     */
    public IDedEntryCache(int maxItems, long refreshTTL) {
        mIdCache = MapUtil.newLruMap(maxItems);
        mRefreshTTL = refreshTTL;
    }

    public synchronized void clear() {
        mIdCache.clear();
    }

    public synchronized void remove(String name, String id) {
        mIdCache.remove(id);
    }
    
    public synchronized void remove(E entry) {
        if (entry != null) {
            mIdCache.remove(entry.getId());
        }
    }
    
    public synchronized void put(E entry) {
        if (entry != null) {
            CacheEntry<E> cacheEntry = new CacheEntry<E>(entry, mRefreshTTL);
            mIdCache.put(entry.getId(), cacheEntry);
        }
    }

    public synchronized void put(List<E> entries, boolean clear) {
        if (entries != null) {
            if (clear) clear();
            for (E e: entries)
                put(e);
        }
    }

    @SuppressWarnings("unchecked")
    private E get(String key, Map cache) {
        CacheEntry<E> ce = (CacheEntry<E>) cache.get(key);
        if (ce != null) {
            if (mRefreshTTL != 0 && ce.isStale()) {
                remove(ce.mEntry);
                return null;
            } else {
                return ce.mEntry;
            }
        } else {
            return null;
        }
    }
    
    public synchronized E getById(String key) {
        return get(key, mIdCache);
    }

}
