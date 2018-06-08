/*
 * 
 */
package com.zimbra.cs.store.http;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.StagedBlob;

public class HttpStagedBlob extends StagedBlob {
    private final String mLocator;
    private boolean mIsInserted;

    protected HttpStagedBlob(Mailbox mbox, String digest, long size, String locator) {
        super(mbox, digest, size);
        mLocator = locator;
    }

    @Override public String getLocator() {
        return mLocator;
    }

    HttpStagedBlob markInserted() {
        mIsInserted = true;
        return this;
    }

    boolean isInserted() {
        return mIsInserted;
    }

    @Override public int hashCode() {
        return mLocator.hashCode();
    }

    @Override public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof HttpStagedBlob))
            return false;
        return mLocator.equals(((HttpStagedBlob) other).mLocator);
    }
}
