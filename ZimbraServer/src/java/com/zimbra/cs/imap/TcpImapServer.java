/*
 * 
 */
package com.zimbra.cs.imap;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.stats.RealtimeStatsCallback;
import com.zimbra.cs.stats.ZimbraPerf;
import com.zimbra.cs.tcpserver.ProtocolHandler;
import com.zimbra.cs.tcpserver.TcpServer;

import java.util.HashMap;
import java.util.Map;

public class TcpImapServer extends TcpServer implements ImapServer, RealtimeStatsCallback {
    public TcpImapServer(ImapConfig config) throws ServiceException {
        super(config.isSslEnabled() ? "ImapSSLServer" : "ImapServer", config);
        ZimbraPerf.addStatsCallback(this);
    }

    @Override protected ProtocolHandler newProtocolHandler() {
        return new TcpImapHandler(this);
    }

    @Override public ImapConfig getConfig() {
        return (ImapConfig) super.getConfig();
    }

    /**
     * Implementation of {@link RealtimeStatsCallback} that returns the number
     * of active handlers and number of threads for this server.
     */
    @Override public Map<String, Object> getStatData() {
        Map<String, Object> data = new HashMap<String, Object>();
        if (getConfig().isSslEnabled()) {
            data.put(ZimbraPerf.RTS_IMAP_SSL_CONN, numActiveHandlers());
            data.put(ZimbraPerf.RTS_IMAP_SSL_THREADS, numThreads());
        } else {
            data.put(ZimbraPerf.RTS_IMAP_CONN, numActiveHandlers());
            data.put(ZimbraPerf.RTS_IMAP_THREADS, numThreads());
        }
        return data;
    }

}
