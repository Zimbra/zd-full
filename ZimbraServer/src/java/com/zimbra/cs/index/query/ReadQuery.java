/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Unread.
 *
 * @author tim
 * @author ysasaki
 */
public final class ReadQuery extends TagQuery {

    public ReadQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Unread", !truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",UNREAD" :",READ");
    }
}
