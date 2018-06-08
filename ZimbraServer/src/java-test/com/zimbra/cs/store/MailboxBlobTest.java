/*
 * 
 */
package com.zimbra.cs.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.store.MailboxBlob.MailboxBlobInfo;

public class MailboxBlobTest {
    @Test
    public void serialization() throws Exception {
        MailboxBlobInfo mbinfo = new MailboxBlobInfo(UUID.randomUUID().toString(), Mailbox.FIRST_USER_ID, 1, "locator");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(mbinfo);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
        MailboxBlobInfo mbi2 = (MailboxBlobInfo) ois.readObject();
        Assert.assertEquals(mbinfo.accountId, mbi2.accountId);
        Assert.assertEquals(mbinfo.itemId, mbi2.itemId);
        Assert.assertEquals(mbinfo.revision, mbi2.revision);
        Assert.assertEquals(mbinfo.locator, mbi2.locator);
    }
}
