/*
 * 
 */
package com.zimbra.cs.mailbox;

public class RevisionInfo {
    private int version;
    private long timestamp;
    private int folderId;
    public RevisionInfo(int version, long timestamp, int folderId) {
        super();
        this.version = version;
        this.timestamp = timestamp;
        this.folderId = folderId;
    }
    public int getVersion() {
        return version;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public int getFolderId() {
        return folderId;
    }
}
