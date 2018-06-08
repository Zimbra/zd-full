/*
 * 
 */
package com.zimbra.cs.index;

import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * A {@link QueryOperation} that filters results out of the result set. The base
 * class is a nop (passes through all hits).
 * <p>
 * Currently used only as a base class for other QueryOps that have to do
 * passthrough/filtering.
 */
abstract class FilterQueryOperation extends QueryOperation {

    protected QueryOperation mOp = null;

    @Override
    protected QueryOperation combineOps(QueryOperation other, boolean union) {
        return null;
    }

    @Override
    protected void depthFirstRecurse(RecurseCallback cb) {
        mOp.depthFirstRecurse(cb);
        cb.recurseCallback(this);
    }

    @Override
    QueryOperation expandLocalRemotePart(Mailbox mbox) throws ServiceException {
        mOp.expandLocalRemotePart(mbox);
        return this;
    }

    @Override
    QueryOperation ensureSpamTrashSetting(Mailbox mbox, boolean includeTrash, boolean includeSpam)
        throws ServiceException {
        return mOp.ensureSpamTrashSetting(mbox, includeTrash, includeSpam);
    }

    @Override
    void forceHasSpamTrashSetting() {
        mOp.forceHasSpamTrashSetting();
    }

    @Override
    QueryTargetSet getQueryTargets() {
        return mOp.getQueryTargets();
    }

    @Override
    boolean hasAllResults() {
        return mOp.hasAllResults();
    }

    @Override
    boolean hasNoResults() {
        return mOp.hasNoResults();
    }

    @Override
    boolean hasSpamTrashSetting() {
        return mOp.hasSpamTrashSetting();
    }

    @Override
    QueryOperation optimize(Mailbox mbox) throws ServiceException {
        // optimize our sub-op, but *don't* optimize us out
        mOp = mOp.optimize(mbox);
        return this;
    }

    @Override
    protected void begin(QueryContext ctx) throws ServiceException {
        assert(context == null);
        context = ctx;
        mOp.begin(ctx);
    }

    @Override
    String toQueryString() {
        return mOp.toQueryString();
    }

    @Override
    public void doneWithSearchResults() throws ServiceException {
        mOp.doneWithSearchResults();
    }

    @Override
    public int estimateResultSize() throws ServiceException {
        return mOp.estimateResultSize();
    }

    @Override
    public ZimbraHit getNext() throws ServiceException {
        ZimbraHit toRet = peekNext();
        if (toRet != null)
            mOp.getNext(); // skip the current hit
        return toRet;
    }

    @Override
    public List<QueryInfo> getResultInfo() {
        return mOp.getResultInfo();
    }

    @Override
    public ZimbraHit peekNext() throws ServiceException {
        return mOp.peekNext();
    }

    @Override
    public void resetIterator() throws ServiceException {
        mOp.resetIterator();
    }
}
