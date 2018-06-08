/*
 * 
 */
package com.zimbra.cs.store.file;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.MailboxBlob;

public class VolumeMailboxBlob extends MailboxBlob {
    private final VolumeBlob blob;

    protected VolumeMailboxBlob(Mailbox mbox, int itemId, int revision, String locator, VolumeBlob blob) {
        super(mbox, itemId, revision, locator);
        this.blob = blob;
    }

    @Override
    public MailboxBlob setSize(long size) {
        super.setSize(size);
        if (blob != null) {
            blob.setRawSize(size);
        }
        return this;
    }

    @Override
    public VolumeBlob getLocalBlob() {
        return blob;
    }
}
