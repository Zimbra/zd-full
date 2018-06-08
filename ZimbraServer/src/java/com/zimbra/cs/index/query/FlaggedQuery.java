/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Flagged.
 *
 * @author tim
 * @author ysasaki
 */
public final class FlaggedQuery extends TagQuery {

    public FlaggedQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Flagged", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",FLAGGED" : ",UNFLAGGED");
    }
}
