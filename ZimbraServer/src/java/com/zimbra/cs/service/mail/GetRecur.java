/* 
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.cs.mailbox.CalendarItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.soap.ZimbraSoapContext;

public class GetRecur extends MailDocumentHandler {

    private static final String[] TARGET_ITEM_PATH = new String[] { MailConstants.A_ID };
    private static final String[] RESPONSE_ITEM_PATH = new String[] { };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_ITEM_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return false; }
    protected String[] getResponseItemPath()  { return RESPONSE_ITEM_PATH; }

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        ItemIdFormatter ifmt = new ItemIdFormatter(zsc);

        ItemId iid = new ItemId(request.getAttribute(MailConstants.A_ID), zsc);
        Element response = getResponseElement(zsc);

        CalendarItem calItem = mbox.getCalendarItemById(octxt, iid.getId());
        ToXML.encodeCalendarItemRecur(response, ifmt, octxt, calItem);
        return response;
    }
}
