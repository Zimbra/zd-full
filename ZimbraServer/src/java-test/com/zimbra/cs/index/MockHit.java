/*
 * 
 */

package com.zimbra.cs.index;

import com.zimbra.cs.mailbox.MailItem;

/**
 * Mock implementation of {@link ZimbraHit} for testing.
 *
 * @author ysasaki
 */
public class MockHit extends ZimbraHit {
    private int id;
    private long date;
    private long size;
    private int convId;
    private MailItem mailItem;
    private String subject;
    private String name;

    public MockHit(int id, String name) {
        super(null, null);
        this.id = id;
        this.name = name;
    }

    @Override
    public int getItemId() {
        return id;
    }

    public void setItemId(int value) {
        id = value;
    }

    @Override
    long getDate() {
        return date;
    }

    public void setDate(long value) {
        date = value;
    }

    @Override
    long getSize() {
        return size;
    }

    public void setSize(long value) {
        size = value;
    }

    @Override
    int getConversationId() {
        return convId;
    }

    void setConversationId(int value) {
        convId = value;
    }

    @Override
    public MailItem getMailItem() {
        return mailItem;
    }

    @Override
    void setItem(MailItem value) {
        mailItem = value;
    }

    @Override
    boolean itemIsLoaded() {
        return mailItem != null;
    }

    @Override
    String getSubject() {
        return subject;
    }

    void setSubject(String value) {
        subject = value;
    }

    @Override
    String getName() {
        return name;
    }

    void setName(String value) {
        name = value;
    }

}
