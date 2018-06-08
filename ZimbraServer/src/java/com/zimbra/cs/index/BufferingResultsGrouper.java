/*
 * 
 */

package com.zimbra.cs.index;

import java.util.LinkedList;
import java.util.List;

import com.zimbra.common.service.ServiceException;

/**
 * Groups hit results for various reasons.
 */
public abstract class BufferingResultsGrouper implements ZimbraQueryResults {

    protected ZimbraQueryResults mHits;
    protected List<ZimbraHit> mBufferedHit = new LinkedList<ZimbraHit>();
    protected boolean atStart = true;


    /**
     * Fills the hit buffer if necessary.  May be called even if the buffer has entries in it,
     * implementation may ignore it (but must return true) in those cases.
     *
     * @return TRUE if there some hits in the buffer, FALSE if not.
     * @throws ServiceException
     *
     */
    protected abstract boolean bufferHits() throws ServiceException;


    public SortBy getSortBy() {
        return mHits.getSortBy();
    }

    public BufferingResultsGrouper(ZimbraQueryResults hits) {
        mHits = hits;
    }

    public void resetIterator() throws ServiceException {
        if (!atStart) {
            mBufferedHit.clear();
            mHits.resetIterator();
            atStart = true;
        }
    }

    public boolean hasNext() throws ServiceException {
        return bufferHits();
    }

    public ZimbraHit getFirstHit() throws ServiceException {
        resetIterator();
        return getNext();
    }

    public ZimbraHit peekNext() throws ServiceException {
        if (bufferHits()) {
            return mBufferedHit.get(0);
        } else {
            return null;
        }
    }

    public ZimbraHit skipToHit(int hitNo) throws ServiceException {
        resetIterator();
        for (int i = 0; i < hitNo; i++) {
            if (!hasNext()) {
                return null;
            }
            getNext();
        }
        return getNext();
    }

    public ZimbraHit getNext() throws ServiceException {
        atStart = false;
        if (bufferHits()) {
            return mBufferedHit.remove(0);
        } else {
            return null;
        }
    }

    public void doneWithSearchResults() throws ServiceException {
        mHits.doneWithSearchResults();
    }

    public List<QueryInfo> getResultInfo() {
        return mHits.getResultInfo();
    }

    public int estimateResultSize() throws ServiceException {
        return mHits.estimateResultSize();
    }

}
