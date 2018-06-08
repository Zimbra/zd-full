/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Invite.
 *
 * @author tim
 * @author ysasaki
 */
public final class InviteQuery extends TagQuery {

    public InviteQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Invite", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",INVITE" : ",NOT_INVITE");
    }
}

