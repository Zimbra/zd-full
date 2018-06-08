/*
 * 
 */
package com.zimbra.cs.session;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.soap.ZimbraSoapContext;

public class RemoteSoapSession extends SoapSession {
    /** Creates a <tt>SoapSession</tt> owned by the given account homed on
     *  a different server.  It thus cannot listen on its own {@link Mailbox}.
     * @see Session#register() */
    public RemoteSoapSession(String authenticatedId, boolean asAdmin) {
        super(authenticatedId, asAdmin);
    }

    @Override protected boolean isMailboxListener() {
        return false;
    }

    @Override protected boolean isIMListener() {
        return false;
    }

    @Override public String getRemoteSessionId(Server server) {
        return null;
    }

    @Override public void putRefresh(Element ctxt, ZimbraSoapContext zsc) {
        ctxt.addUniqueElement(ZimbraNamespace.E_REFRESH);
        return;
    }

    @Override public Element putNotifications(Element ctxt, ZimbraSoapContext zsc, int lastSequence) {
        if (ctxt == null)
            return null;

        QueuedNotifications ntfn;
        synchronized (mSentChanges) {
            if (!mChanges.hasNotifications())
                return null;
            ntfn = mChanges;
            mChanges = new QueuedNotifications(ntfn.getSequence() + 1);
        }

        putQueuedNotifications(null, ntfn, ctxt, zsc);
        return ctxt;
    }
}
