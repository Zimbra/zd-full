/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Forwarded.
 *
 * @author tim
 * @author ysasaki
 */
public final class ForwardedQuery extends TagQuery {

    public ForwardedQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Forwarded", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",FORWARDED" : ",UNFORWARDED");
    }
}
