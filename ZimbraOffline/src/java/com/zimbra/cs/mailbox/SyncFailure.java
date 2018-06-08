/*
 * 
 */
package com.zimbra.cs.mailbox;

public class SyncFailure {
    private long itemId;
    private Exception exception;
    private String message;
    public SyncFailure(long itemId, Exception exception, String message) {
        super();
        this.itemId = itemId;
        this.exception = exception;
        this.message = message;
    }
    public long getItemId() {
        return itemId;
    }
    public Exception getException() {
        return exception;
    }
    public String getMessage() {
        return message;
    }
}
