/*
 * 
 */

package com.zimbra.cs.redolog.op;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import com.zimbra.common.mime.MimeConstants;

public class RedoableOpDataSource implements DataSource {

    private RedoableOpData mData;
    
    public RedoableOpDataSource(RedoableOpData data) {
        if (data == null) {
            throw new NullPointerException();
        }
        mData = data;
    }
    
    public String getContentType() {
        return MimeConstants.CT_APPLICATION_OCTET_STREAM;
    }

    public InputStream getInputStream() throws IOException {
        return mData.getInputStream();
    }

    public String getName() {
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new IOException("not supported");
    }
}
