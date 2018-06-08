/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Sent.
 *
 * @author tim
 * @author ysasaki
 */
public final class SentQuery extends TagQuery {

    public SentQuery(Mailbox mailbox, boolean truth)
        throws ServiceException {

        super(mailbox, "\\Sent", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",SENT" : ",RECEIVED");
    }
}
