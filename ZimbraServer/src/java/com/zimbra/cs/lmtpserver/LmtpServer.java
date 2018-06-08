/*
 * 
 */
package com.zimbra.cs.lmtpserver;

import com.zimbra.cs.server.Server;

public interface LmtpServer extends Server {
    public LmtpConfig getConfig();
}
