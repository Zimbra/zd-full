/*
 * 
 */

package com.zimbra.cs.mailbox;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.acl.EffectiveACLCache;
import com.zimbra.cs.mailbox.calendar.cache.CalendarCacheManager;
import com.zimbra.cs.memcached.MemcachedConnector;
import com.zimbra.cs.session.PendingModifications;

public class MemcachedCacheManager {

    public static void purgeMailbox(Mailbox mbox) throws ServiceException {
        CalendarCacheManager.getInstance().purgeMailbox(mbox);
        EffectiveACLCache.getInstance().purgeMailbox(mbox);
        FoldersTagsCache.getInstance().purgeMailbox(mbox);
    }

    public static void notifyCommittedChanges(PendingModifications mods, int changeId) {
        // We have to notify calendar cache before checking memcached connectedness
        // because a portion of calendar cache is not memcached-based.
        CalendarCacheManager.getInstance().notifyCommittedChanges(mods, changeId);
        if (MemcachedConnector.isConnected()) {
            EffectiveACLCache.getInstance().notifyCommittedChanges(mods, changeId);
            FoldersTagsCache.getInstance().notifyCommittedChanges(mods, changeId);
        }
    }
}
