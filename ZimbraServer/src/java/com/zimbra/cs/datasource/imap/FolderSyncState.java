/*
 * 
 */
package com.zimbra.cs.datasource.imap;

final class FolderSyncState {
    private long lastFetchedUid;
    private long lastUidNext;
    private int lastChangeId;

    public long getLastFetchedUid() {
        return lastFetchedUid;
    }

    public long getLastUidNext() {
        return lastUidNext;
    }

    public int getLastChangeId() {
        return lastChangeId;
    }

    public void setLastFetchedUid(long uid) {
        lastFetchedUid = uid;
    }

    public void setLastUidNext(long lastUidNext) {
        this.lastUidNext = lastUidNext;
    }

    public void setLastChangeId(int lastChangeId) {
        this.lastChangeId = lastChangeId;
    }

    public void updateLastFetchedUid(long uid) {
        if (uid > lastFetchedUid) {
            lastFetchedUid = uid;
        }
    }

    public String toString() {
        return String.format(
            "{lastFetchedUid=%d,lastUidNext=%d,lastChangeId=%d}",
            lastFetchedUid, lastUidNext, lastChangeId);
    }
}
