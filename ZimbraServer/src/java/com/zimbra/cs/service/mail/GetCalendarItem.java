/*
 * 
 */

package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * @author tim
 */
public class GetCalendarItem extends CalendarRequest {
    private static Log sLog = LogFactory.getLog(GetCalendarItem.class);

    private static final String[] TARGET_ITEM_PATH = new String[] { MailConstants.A_ID };
    private static final String[] RESPONSE_ITEM_PATH = new String[] { };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_ITEM_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return false; }
    protected String[] getResponseItemPath()  { return RESPONSE_ITEM_PATH; }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        ItemIdFormatter ifmt = new ItemIdFormatter(zsc);

        boolean sync = request.getAttributeBool(MailConstants.A_SYNC, false);
        boolean includeContent = request.getAttributeBool(MailConstants.A_CAL_INCLUDE_CONTENT, false);
        ItemId iid = null;
        String uid = request.getAttribute(MailConstants.A_UID, null);
        String id = request.getAttribute(MailConstants.A_ID, null);
        if (uid != null) {
            if (id != null)
                throw ServiceException.INVALID_REQUEST("either id or uid should be specified, but not both", null);
            sLog.info("<GetCalendarItem uid=" + uid + "> " + zsc);
        } else {
            iid = new ItemId(id, zsc);
            sLog.info("<GetCalendarItem id=" + iid.getId() + "> " + zsc);
        }

        // want to return modified date only on sync-related requests
        int fields = ToXML.NOTIFY_FIELDS;
        if (sync)
            fields |= Change.MODIFIED_CONFLICT;

        Element response = getResponseElement(zsc);
        synchronized(mbox) {
            CalendarItem calItem;
            if (uid != null) {
                calItem = mbox.getCalendarItemByUid(octxt, uid);
                if (calItem == null)
                    throw MailServiceException.NO_SUCH_CALITEM(uid);
            } else {
                calItem = mbox.getCalendarItemById(octxt, iid.getId());
                if (calItem == null)
                    throw MailServiceException.NO_SUCH_CALITEM(iid.getId());
            }
            ToXML.encodeCalendarItemSummary(response, ifmt, octxt, calItem, fields, true, includeContent);
        }

        return response;
    }
}
