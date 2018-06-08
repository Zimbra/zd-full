/*
 * 
 */
package com.zimbra.cs.nio;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.server.Server;
import com.zimbra.cs.server.ServerConfig;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutorService;

public abstract class NioServer implements Server {
    private final ServerConfig config;
    private final ExecutorService handlerPool;
    private SSLInfo sslInfo;

    protected NioServer(ServerConfig config, ExecutorService pool) {
        this.config = config;
        handlerPool = pool;
    }

    public SSLInfo getSSLInfo() {
        if (sslInfo == null) {
            sslInfo = new SSLInfo(getConfig());
        }
        return sslInfo;
    }

    public ServerConfig getConfig() {
        return config;
    }

    protected ExecutorService getHandlerPool() {
        return handlerPool;
    }

    public abstract NioStatsMBean getStats();

    public void stop() throws ServiceException {
        stop(getConfig().getShutdownGraceSeconds());
    }
    
    protected void registerStatsMBean(String type) {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            mbs.registerMBean(getStats(), new ObjectName("ZimbraCollaborationSuite:type=" + type));
        } catch (Exception e) {
            // getLog().warn("Unable to register MinaStats mbean", e);
        }
    }
}
