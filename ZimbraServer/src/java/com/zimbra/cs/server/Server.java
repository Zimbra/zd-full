/*
 * 
 */

package com.zimbra.cs.server;

import com.zimbra.common.service.ServiceException;

/*
 * Common interface for servers based on either TcpServer or MinaServer.
 */
public interface Server {
    ServerConfig getConfig();
    void start() throws ServiceException;
    void stop() throws ServiceException;
    void stop(int graceSecs) throws ServiceException;
}
