/*
 * 
 */
package com.zimbra.cs.pop3;

import com.zimbra.cs.server.Server;

public interface Pop3Server extends Server {
    Pop3Config getConfig();
}
