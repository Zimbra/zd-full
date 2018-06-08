/*
 * 
 */

/*
 * Created on 2004. 10. 12.
 */
package com.zimbra.cs.store;

import java.io.IOException;
import java.io.Serializable;

import com.zimbra.cs.mailbox.Mailbox;

public abstract class MailboxBlob {
    public static class MailboxBlobInfo implements Serializable {
        private static final long serialVersionUID = 6378518636677970767L;

        public String accountId;
        public int itemId;
        public int revision;
        public String locator;

        public MailboxBlobInfo(String accountId, int itemId, int revision, String locator) {
            this.accountId = accountId;
            this.itemId = itemId;
            this.revision = revision;
            this.locator = locator;
        }
    }

    private final Mailbox mailbox;

    private final int itemId;
    private final int revision;
    private final String mLocator;
    protected Long size;
    protected String digest;

    protected MailboxBlob(Mailbox mbox, int itemId, int revision, String locator) {
        this.mailbox = mbox;
        this.itemId = itemId;
        this.revision = revision;
        mLocator = locator;
    }

    public int getItemId() {
        return itemId;
    }

    public int getRevision() {
        return revision;
    }

    public String getLocator() {
        return mLocator;
    }

    public String getDigest() throws IOException {
        if (digest == null) {
            digest = getLocalBlob().getDigest();
        }
        return digest;
    }

    public MailboxBlob setDigest(String digest) {
        this.digest = digest;
        return this;
    }

    public long getSize() throws IOException {
        if (size == null) {
            this.size = new Long(getLocalBlob().getRawSize());
        }
        return size;
    }

    public MailboxBlob setSize(long size) {
        this.size = size;
        return this;
    }

    public Mailbox getMailbox() {
        return mailbox;
    }

    abstract public Blob getLocalBlob() throws IOException;

    @Override
    public String toString() {
        return mailbox.getId() + ":" + itemId + ":" + revision + "[" + getLocator() + "]";
    }
}
