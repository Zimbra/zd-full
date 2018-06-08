/*
 * 
 */
package com.zimbra.cs.milter;

import com.zimbra.cs.server.Server;

public interface MilterServer extends Server {
    @Override public MilterConfig getConfig(); 
}
