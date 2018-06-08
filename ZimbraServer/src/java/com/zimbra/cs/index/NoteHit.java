/*
 * 
 */

package com.zimbra.cs.index;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Note;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailItem;

/**
 * @since Nov 9, 2004
 * @author tim
 */
public final class NoteHit extends ZimbraHit {
    private Note mNote = null;
    private int mMailItemId;

    NoteHit(ZimbraQueryResultsImpl results, Mailbox mbx, int mailItemId, Note note) {
        super(results, mbx);
        mMailItemId = mailItemId;
        mNote = note;
    }

    @Override
    public long getDate() throws ServiceException {
        if (mCachedDate == -1) {
            mCachedDate = getNote().getDate();
        }
        return mCachedDate;
    }

    @Override
    public MailItem getMailItem() throws ServiceException {
        return getNote();
    }

    public Note getNote() throws ServiceException {
        if (mNote == null) {
            mNote = getMailbox().getNoteById(null, getItemId());
        }
        return mNote;
    }

    @Override
    void setItem(MailItem item) {
        mNote = (Note) item;
    }

    @Override
    boolean itemIsLoaded() {
        return mNote != null;
    }

    @Override
    public String getSubject() throws ServiceException {
        if (mCachedSubj == null) {
            mCachedSubj = getNote().getSubject();
        }
        return mCachedSubj;
    }

    @Override
    public String getName() throws ServiceException {
        if (mCachedName == null) {
            mCachedName = getNote().getSubject();
        }
        return mCachedName;
    }

    @Override
    public int getConversationId() {
        return 0;
    }

    @Override
    public int getItemId() {
        return mMailItemId;
    }

    public byte getItemType() {
        return MailItem.TYPE_NOTE;
    }

    @Override
    public long getSize() throws ServiceException {
        return getNote().getSize();
    }

    @Override
    public String toString() {
        int convId = getConversationId();
        String msgStr = "";
        String noteStr = "";
        try {
            msgStr = Integer.toString(getItemId());
            noteStr = getNote().toString();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "NT: " + super.toString() + " C" + convId + " M" + msgStr + " " + noteStr;
    }

    public int getHitType() {
        return 4;
    }

    public int doitVirt() {
        return 0;
    }

}
