/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

public class YabException extends Exception {
    private final ErrorResult error;

    public YabException(ErrorResult error) {
        super(error.toString());
        this.error = error;
    }

    public YabException(String msg) {
        super(msg);
        error = null;
    }

    public ErrorResult getErrorResult() {
        return error;
    }
}
