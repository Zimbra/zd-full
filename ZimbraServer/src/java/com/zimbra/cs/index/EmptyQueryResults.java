/*
 * 
 */

/*
 * Created on Oct 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.cs.index;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * @author tim
 *
 */
class EmptyQueryResults extends ZimbraQueryResultsImpl {

    
    EmptyQueryResults(byte[] types, SortBy searchOrder, Mailbox.SearchResultMode mode) {
        super(types, searchOrder, mode);
    }
    
    public void resetIterator()  {
    }

    public ZimbraHit getNext() {
        return null;
    }

    public ZimbraHit peekNext() {
        return null;
    }
    
    public void doneWithSearchResults() {
    }

    public ZimbraHit skipToHit(int hitNo) {
        return null;
    }
    
    public List<QueryInfo> getResultInfo() { return new ArrayList<QueryInfo>(); }
    
    public int estimateResultSize() throws ServiceException { return 0; }
    
}
