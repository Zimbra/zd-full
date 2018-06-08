/*
 * 
 */

package com.zimbra.cs.index;

import java.util.Collections;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

/**
 * Take {@link ZimbraHit}s which are already sorted by sort-order and
 * additionally sort them by mail-item-id
 * <p>
 * This Grouper has no effect if the current sort mode is "none"
 *
 * @since Mar 15, 2005
 * @author tim
 */
public class HitIdGrouper extends BufferingResultsGrouper {
    private SortBy mSortOrder;

    public static ZimbraQueryResults Create(ZimbraQueryResults hits, SortBy sortOrder) {
        if (sortOrder == SortBy.NONE) {
            return hits;
        } else {
            return new HitIdGrouper(hits, sortOrder);
        }
    }

    private HitIdGrouper(ZimbraQueryResults hits, SortBy sortOrder) {
        super(hits);
        mSortOrder = sortOrder;
    }

    @Override
    public boolean hasNext() throws ServiceException {
        return (mBufferedHit.size() > 0 || mHits.hasNext());
    }

    @Override
    protected boolean bufferHits() throws ServiceException {
        if (mBufferedHit.size() > 0){
            return true;
        }

        if (!mHits.hasNext()) {
            return false;
        }

        ZimbraHit curGroupHit = mHits.getNext();
        mBufferedHit.add(curGroupHit);

        // buffer all the hits with the same sort field
        while (mHits.hasNext() &&
                curGroupHit.compareBySortField(mSortOrder, mHits.peekNext()) == 0) {
            if (ZimbraLog.index_search.isDebugEnabled()) {
                ZimbraLog.index_search.debug("HitIdGrouper buffering " + mHits.peekNext());
            }
            mBufferedHit.add(mHits.getNext());
        }

        // sort them by mail-item-id
        Collections.sort(mBufferedHit,
                ZimbraHit.getSortAndIdComparator(mSortOrder));

        // we're done
        return true;
    }
}
