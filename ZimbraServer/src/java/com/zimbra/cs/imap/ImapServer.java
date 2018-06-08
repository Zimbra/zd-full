/*
 * 
 */
package com.zimbra.cs.imap;

import com.zimbra.cs.server.Server;

public interface ImapServer extends Server {
    @Override public ImapConfig getConfig();
}
