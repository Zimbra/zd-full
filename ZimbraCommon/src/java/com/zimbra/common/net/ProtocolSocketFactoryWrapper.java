/*
 * 
 */
package com.zimbra.common.net;

import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

class ProtocolSocketFactoryWrapper implements ProtocolSocketFactory {
    private final SocketFactory factory;

    ProtocolSocketFactoryWrapper(SocketFactory factory) {
        this.factory = factory;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress,
            int localPort) throws IOException {
        return factory.createSocket(host, port, localAddress, localPort);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress,
            int localPort, HttpConnectionParams params) throws IOException {
        int timeout = params != null ? params.getConnectionTimeout() : 0;
        if (timeout > 0) {
            Socket sock = factory.createSocket();
            sock.bind(new InetSocketAddress(localAddress, localPort));
            sock.connect(new InetSocketAddress(host, port), timeout);
            return sock;
        } else {
            return factory.createSocket(host, port, localAddress, localPort);
        }
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return factory.createSocket(host, port);
    }
}
