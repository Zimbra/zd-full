/*
 * 
 */
package com.zimbra.cs.store.http;

import java.io.IOException;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StoreManager;

public class HttpMailboxBlob extends MailboxBlob {
    protected HttpMailboxBlob(Mailbox mbox, int itemId, int revision, String locator) {
        super(mbox, itemId, revision, locator);
    }

    @Override
    public Blob getLocalBlob() throws IOException {
        HttpStoreManager hsm = (HttpStoreManager) StoreManager.getInstance();
        Blob blob = hsm.getLocalBlob(getMailbox(), getLocator(), size == null ? -1 : size.intValue());

        setSize(blob.getRawSize());
        if (digest != null) {
            setDigest(blob.getDigest());
        }
        return blob;
    }

    @Override
    public int hashCode() {
        return getLocator().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof HttpMailboxBlob)) {
            return false;
        }
        return getLocator().equals(((HttpMailboxBlob) other).getLocator());
    }
}
