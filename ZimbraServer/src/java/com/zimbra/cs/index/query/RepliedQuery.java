/*
 * 
 */
package com.zimbra.cs.index.query;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Mailbox;

/**
 * Query messages tagged with Answered.
 *
 * @author tim
 * @author ysasaki
 */
public final class RepliedQuery extends TagQuery {

    public RepliedQuery(Mailbox mailbox, boolean truth) throws ServiceException {
        super(mailbox, "\\Answered", truth);
    }

    @Override
    public void dump(StringBuilder out) {
        super.dump(out);
        out.append(getBool() ? ",REPLIED" : ",UNREPLIED");
    }
}
