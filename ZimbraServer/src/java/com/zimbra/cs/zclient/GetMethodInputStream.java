/*
 * 
 */
package com.zimbra.cs.zclient;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.httpclient.methods.GetMethod;

/**
 * Wraps a HTTPClient <tt>GetMethod</tt> and automatically releases resources
 * when the stream is closed.
 */
public class GetMethodInputStream extends InputStream {

    private GetMethod mGetMethod;
    private InputStream mIn;
    
    public GetMethodInputStream(GetMethod getMethod)
    throws IOException {
        mGetMethod = getMethod;
        mIn = getMethod.getResponseBodyAsStream();
    }
    
    @Override
    public int read() throws IOException {
        return mIn.read();
    }

    @Override
    public int available() throws IOException {
        return mIn.available();
    }

    @Override
    public void close() throws IOException {
        mIn.close();
        mGetMethod.releaseConnection();
    }

    @Override
    public synchronized void mark(int readlimit) {
        mIn.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return mIn.markSupported();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return mIn.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return mIn.read(b);
    }

    @Override
    public synchronized void reset() throws IOException {
        mIn.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return mIn.skip(n);
    }
}
