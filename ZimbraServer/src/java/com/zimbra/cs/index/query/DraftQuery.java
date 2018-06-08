/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Draft.
 *
 * @author tim
 * @author ysasaki
 */
public final class DraftQuery extends TagQuery {

    public DraftQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Draft", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",DRAFT" : ",UNDRAFT");
    }
}
