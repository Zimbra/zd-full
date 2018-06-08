/*
 * 
 */
package com.zimbra.common.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.httpclient.methods.RequestEntity;

public class BufferStreamRequestEntity extends BufferStream implements
    RequestEntity {
    String contentType;
    private InputStream is = null;

    public BufferStreamRequestEntity() { this(0); }

    public BufferStreamRequestEntity(long sizeHint) {
        this(sizeHint, Integer.MAX_VALUE);
    }

    public BufferStreamRequestEntity(long sizeHint, int maxBuffer) {
        this(sizeHint, maxBuffer, Long.MAX_VALUE);
    }

    public BufferStreamRequestEntity(long sizeHint, int maxBuffer, long maxSize) {
        this(null, null, sizeHint, maxBuffer, maxSize);
    }

    public BufferStreamRequestEntity(InputStream is) { this(is, 0); }

    public BufferStreamRequestEntity(InputStream is, long sizeHint) {
        this(is, sizeHint, Integer.MAX_VALUE);
    }

    public BufferStreamRequestEntity(InputStream is, long sizeHint, int maxBuffer) {
        this(is, null, sizeHint, maxBuffer, Long.MAX_VALUE);
    }

    public BufferStreamRequestEntity(InputStream is, String contentType) {
        this(is, contentType, 0);
    }

    public BufferStreamRequestEntity(InputStream is, String contentType, long
        sizeHint) {
        this(is, contentType, sizeHint, Integer.MAX_VALUE);
    }

    public BufferStreamRequestEntity(InputStream is, String contentType,
        long sizeHint, int maxBuffer) {
        this(is, contentType, sizeHint, maxBuffer, Long.MAX_VALUE);
    }

    public BufferStreamRequestEntity(String contentType) { this(contentType, 0); }

    public BufferStreamRequestEntity(String contentType, long sizeHint) {
        this(contentType, sizeHint, Integer.MAX_VALUE);
    }

    public BufferStreamRequestEntity(String contentType, long sizeHint, int
        maxBuffer) {
        this(null, contentType, sizeHint, maxBuffer, Long.MAX_VALUE);
    }

    public BufferStreamRequestEntity(InputStream is, String contentType, long
        sizeHint, int maxBuffer, long maxSize) {
        super(sizeHint, maxBuffer, maxSize);
        this.contentType = contentType;
        this.is = is;
    }

    public long getContentLength() {
        readData();
        return getSize();
    }

    public String getContentType() { return contentType; }

    public boolean isRepeatable() { return true; }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private void readData() {
        if (is != null) {
            try {
                readFrom(is);
            } catch (IOException e) {
            }
            is = null;
        }
    }
    
    public void writeRequest(final OutputStream os) throws IOException {
        readData();
        copyTo(os);
    }
}
