/*
 * 
 */
package com.zimbra.cs.mailbox;

import com.zimbra.cs.fb.FreeBusyProvider;
import com.zimbra.cs.localconfig.DebugConfig;
import com.zimbra.cs.mailbox.alerts.CalItemReminderService;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.util.ZimbraApplication;

import java.util.HashSet;


public abstract class MailboxListener {

	
	public abstract void handleMailboxChange(String accountId, PendingModifications mods, OperationContext octxt, int lastChangeId);
	public abstract int registerForItemTypes();
	
	
	private static final HashSet<MailboxListener> sListeners;
	
	static {
		sListeners = new HashSet<MailboxListener>();
        if (ZimbraApplication.getInstance().supports(CalItemReminderService.class) && !DebugConfig.disableCalendarReminderEmail) {
            register(new CalItemReminderService());
        }
    }
	
	
	public static void register(MailboxListener listener) {
		synchronized (sListeners) {
			sListeners.add(listener);
		}
	}
	
    public static void mailboxChanged(String accountId, PendingModifications mods, OperationContext octxt, int lastChangeId) {
        // if the calendar items has changed in the mailbox,
        // recalculate the free/busy for the user and propogate to
        // other system.
        FreeBusyProvider.mailboxChanged(accountId, mods.changedTypes);

        MemcachedCacheManager.notifyCommittedChanges(mods, lastChangeId);

        for (MailboxListener l : sListeners) {
            if ((mods.changedTypes & l.registerForItemTypes()) > 0) {
                l.handleMailboxChange(accountId, mods, octxt, lastChangeId);
            }
        }
    }
}
