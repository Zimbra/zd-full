/*
 * 
 */
package com.zimbra.cs.lmtpserver;

import java.util.HashMap;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.stats.RealtimeStatsCallback;
import com.zimbra.cs.stats.ZimbraPerf;
import com.zimbra.cs.tcpserver.ProtocolHandler;
import com.zimbra.cs.tcpserver.TcpServer;

public class TcpLmtpServer extends TcpServer implements LmtpServer, RealtimeStatsCallback {
    public TcpLmtpServer(LmtpConfig config) throws ServiceException {
        super("LmtpServer", config);
        ZimbraPerf.addStatsCallback(this);
    }

    @Override
    protected ProtocolHandler newProtocolHandler() {
        return new TcpLmtpHandler(this);
    }

    @Override
    public LmtpConfig getConfig() {
        return (LmtpConfig) super.getConfig();
    }

    /**
     * Implementation of {@link RealtimeStatsCallback} that returns the number
     * of active handlers and number of threads for this server.
     */
    @Override
    public Map<String, Object> getStatData() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(ZimbraPerf.RTS_LMTP_CONN, numActiveHandlers());
        data.put(ZimbraPerf.RTS_LMTP_THREADS, numThreads());
        return data;
    }
}
