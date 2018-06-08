/*
 * 
 */

package com.zimbra.cs.index;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * @since Feb 15, 2005
 */
public class CalendarItemHit extends ZimbraHit {

    protected int mId;
    protected CalendarItem mCalItem;
    private byte mType = MailItem.TYPE_UNKNOWN;

    CalendarItemHit(ZimbraQueryResultsImpl results, Mailbox mbx, int id, CalendarItem cal) {
        super(results, mbx);
        mId = id;
        mCalItem = cal;
        if (cal != null) {
            mType = cal.getType();
        }
    }

    CalendarItemHit(ZimbraQueryResultsImpl results, Mailbox mbx, int id, CalendarItem cal, byte type) {
        super(results, mbx);
        mId = id;
        mCalItem = cal;
        mType = type;
    }

    @Override
    public MailItem getMailItem() throws ServiceException {
        return getCalendarItem();
    }

    public CalendarItem getCalendarItem() throws ServiceException {
        if (mCalItem == null) {
            mCalItem = this.getMailbox().getCalendarItemById(null, mId);
        }
        return mCalItem;
    }

    @Override
    public long getDate() throws ServiceException {
        return getCalendarItem().getDate();
    }

    @Override
    public long getSize() throws ServiceException {
        return getCalendarItem().getSize();
    }

    @Override
    public int getConversationId() {
        assert(false);
        return 0;
    }

    @Override
    public int getItemId() {
        return mId;
    }

    public byte getItemType() {
        return mType;
    }

    @Override
    void setItem(MailItem item) {
        mCalItem = (CalendarItem)item;
        if (mCalItem != null) {
            mType = mCalItem.getType();
        } else {
            mType = MailItem.TYPE_UNKNOWN;
        }
    }

    @Override
    boolean itemIsLoaded() {
        return (mId == 0) || (mCalItem != null);
    }

    @Override
    public String getSubject() throws ServiceException {
        return getCalendarItem().getSubject();
    }

    @Override
    public String getName() throws ServiceException {
        return getCalendarItem().getSubject();
    }

    @Override
    public String toString() {
        String name= "";
        String subject= "";
        try {
            name = getName();
        } catch(Exception e) {
        }
        try {
            subject=getSubject();
        } catch(Exception e) {
        }
        return "CalendarItem: " + super.toString() + " " + name + " " + subject;
    }
}
