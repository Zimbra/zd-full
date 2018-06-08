/*
 * 
 */

package com.zimbra.cs.index;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailItem;

/**
 * @since Nov 8, 2004
 * @author tim
 */
public final class ContactHit extends ZimbraHit {
    private Contact mContact = null;
    private int mItemId;

    public ContactHit(ZimbraQueryResultsImpl results, Mailbox mbx, int itemId, Contact contact) {
        super(results, mbx);
        mItemId = itemId;
        mContact = contact;
    }

    @Override
    public long getDate() throws ServiceException {
        if (mCachedDate == -1) {
            mCachedDate = getContact().getDate();
        }
        return mCachedDate;
    }

    @Override
    public MailItem getMailItem() throws ServiceException {
        return getContact();
    }

    public Contact getContact() throws ServiceException {
        if (mContact == null) {
            mContact = getMailbox().getContactById(null, getItemId());
        }
        return mContact;
    }

    @Override
    public long getSize() throws ServiceException {
        return getContact().getSize();
    }

    @Override
    public int getConversationId() {
        return 0;
    }

    @Override
    public int getItemId() {
        return mItemId;
    }

    public byte getItemType() {
        return MailItem.TYPE_CONTACT;
    }

    @Override
    void setItem(MailItem item) {
        mContact = (Contact) item;
    }

    @Override
    boolean itemIsLoaded() {
        return mContact != null;
    }

    @Override
    public String getSubject() throws ServiceException {
        if (mCachedSubj == null) {
            mCachedSubj = getContact().getSubject();
        }
        return mCachedSubj;
    }

    @Override
    public String getName() throws ServiceException {
        if (mCachedName == null) {
            mCachedName = getContact().getSortName();
        }
        return mCachedName;
    }

    @Override
    public String toString() {
        int convId = getConversationId();
        String msgStr = "";
        String contactStr = "";
        try {
            msgStr = Integer.toString(getItemId());
            contactStr = getContact().toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "CT: " + super.toString() + " C" + convId + " M" + msgStr + " " + contactStr;
    }

}
