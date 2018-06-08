/*
 * 
 */
package com.zimbra.common.net;

import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

class SecureProtocolSocketFactoryWrapper
    extends ProtocolSocketFactoryWrapper implements SecureProtocolSocketFactory {
    
    private SSLSocketFactory factory;

    SecureProtocolSocketFactoryWrapper(SSLSocketFactory factory) {
        super(factory);
        this.factory = factory;
    }
    
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return factory.createSocket(socket, host, port, autoClose);
    }
}
