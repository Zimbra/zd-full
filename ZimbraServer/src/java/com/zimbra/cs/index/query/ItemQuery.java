/*
 * 
 */
package com.zimbra.cs.index.query;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.index.DBQueryOperation;
import com.zimbra.cs.index.NoResultsQueryOperation;
import com.zimbra.cs.index.QueryOperation;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.service.util.ItemId;

/**
 * Query by item ID.
 *
 * @author tim
 * @author ysasaki
 */
public final class ItemQuery extends Query {

    private boolean mIsAllQuery;
    private boolean mIsNoneQuery;
    private List<ItemId> mItemIds;
    private Mailbox mMailbox;

    public static Query create(Mailbox mbox, String str) throws ServiceException {

        boolean allQuery = false;
        boolean noneQuery = false;
        List<ItemId> itemIds = new ArrayList<ItemId>();

        if (str.equalsIgnoreCase("all")) {
            allQuery = true;
        } else if (str.equalsIgnoreCase("none")) {
            noneQuery = true;
        } else {
            String[] items = str.split(",");
            for (int i = 0; i < items.length; i++) {
                if (items[i].length() > 0) {
                    ItemId iid = new ItemId(items[i], mbox.getAccountId());
                    itemIds.add(iid);
                }
            }
            if (itemIds.size() == 0) {
                noneQuery = true;
            }
        }

        return new ItemQuery(mbox, allQuery, noneQuery, itemIds);
    }

    ItemQuery(Mailbox mbox, boolean all, boolean none, List<ItemId> ids) {
        mIsAllQuery = all;
        mIsNoneQuery = none;
        mItemIds = ids;
        mMailbox = mbox;
    }

    @Override
    public QueryOperation getQueryOperation(boolean bool) {
        DBQueryOperation dbOp = new DBQueryOperation();

        bool = evalBool(bool);

        if (bool && mIsAllQuery || !bool && mIsNoneQuery) {
            // adding no constraints should match everything...
        } else if (bool && mIsNoneQuery || !bool && mIsAllQuery) {
            return new NoResultsQueryOperation();
        } else {
            for (ItemId iid : mItemIds) {
                dbOp.addItemIdClause(mMailbox, iid, bool);
            }
        }
        return dbOp;
    }

    @Override
    public void dump(StringBuilder out) {
        out.append("ITEMID");
        if (mIsAllQuery) {
            out.append(",all");
        } else if (mIsNoneQuery) {
            out.append(",none");
        } else {
            for (ItemId id : mItemIds) {
                out.append(',');
                out.append(id.toString());
            }
        }
    }
}
