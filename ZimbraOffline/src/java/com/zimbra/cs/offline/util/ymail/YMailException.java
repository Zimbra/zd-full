/*
 * 
 */
package com.zimbra.cs.offline.util.ymail;

import java.io.IOException;

public class YMailException extends IOException {
    private YMailError error;

    public YMailException(String msg, Throwable cause) {
        super(msg);
        initCause(cause);
    }

    public YMailException(String msg) {
        super(msg);
    }

    public void setError(YMailError error) {
        this.error = error;
    }
    
    public YMailError getError() {
        return error;
    }

    public boolean isRetriable() {
        return error != null && error.isRetriable();
    }
}
