/*
 * 
 */
package com.zimbra.cs.index;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;

/**
 * Reference to {@link IndexSearcher} that supports reference count.
 */
class IndexSearcherRef {
    private Searcher mSearcher;
    private IndexReaderRef mReader;
    private int mCount = 1;
    private Sort mSort = null;

    IndexSearcherRef(IndexReaderRef reader) {
        mReader= reader;
        mSearcher = new IndexSearcher(mReader.getReader());
    }

    synchronized void setSort(Sort sort) {
        mSort = sort;
    }

    synchronized Sort getSort() {
        return mSort;
    }

    synchronized Searcher getSearcher() {
        return mSearcher;
    }

    synchronized IndexReader getReader() {
        return mReader.getReader();
    }

    synchronized void forceClose() {
        mReader.forceClose();
        mReader = null;
    }

    /**
     * Decrements the reference counter.
     * <p>
     * When the reference counter reached to 0, it closes the underlying
     * {@link IndexReader}.
     */
    synchronized void dec() {
        mSort = null;
        mCount--;
        assert(mCount >= 0);
        if (0 == mCount) {
            mReader.dec();
            mReader = null;
        }
    }

    /**
     * Increments the reference counter.
     *
     * @return underlying {@link IndexSearcher} object
     */
    synchronized void inc() {
        assert(mCount > 0);
        mCount++;
    }

    synchronized TopDocs search(Query query, Filter filter, int num) throws IOException {
        if (mSort == null) {
            return getSearcher().search(query, filter, num);
        } else {
            return getSearcher().search(query, filter, num, mSort);
        }
    }
}
