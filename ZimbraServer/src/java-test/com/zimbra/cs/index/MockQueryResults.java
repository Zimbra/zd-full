/*
 * 
 */

package com.zimbra.cs.index;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;

/**
 * Mock implementation of {@link ZimbraQueryResults} for testing.
 *
 * @author ysasaki
 */
public class MockQueryResults implements ZimbraQueryResults {

    private final SortBy sortOrder;
    private List<ZimbraHit> hits = new ArrayList<ZimbraHit>();
    private int next = 0;
    private final List<QueryInfo> queryInfo = new ArrayList<QueryInfo>();

    public MockQueryResults(SortBy sort) {
        sortOrder = sort;
    }

    public void add(ZimbraHit hit) {
        hits.add(hit);
    }

    @Override
    public void resetIterator() {
        next = 0;
    }

    @Override
    public ZimbraHit getNext() {
        return hits.get(next++);
    }

    @Override
    public ZimbraHit peekNext() {
        return hits.get(next);
    }

    @Override
    public ZimbraHit getFirstHit() throws ServiceException {
        resetIterator();
        return getNext();
    }

    @Override
    public ZimbraHit skipToHit(int hitNo) throws ServiceException {
        next = hitNo;
        return getNext();
    }

    @Override
    public boolean hasNext() throws ServiceException {
        return next < hits.size();
    }

    @Override
    public void doneWithSearchResults() throws ServiceException {
        hits = null;
    }

    @Override
    public SortBy getSortBy() {
        return sortOrder;
    }

    @Override
    public List<QueryInfo> getResultInfo() {
        return queryInfo;
    }

    @Override
    public int estimateResultSize() throws ServiceException {
        return hits.size();
    }

}
