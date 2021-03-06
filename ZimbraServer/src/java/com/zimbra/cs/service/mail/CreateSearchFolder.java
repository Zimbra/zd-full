/*
 * 
 */

/*
 * Created on May 26, 2004
 */
package com.zimbra.cs.service.mail;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.Flag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.SearchFolder;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.soap.ZimbraSoapContext;

/**
 */
public class CreateSearchFolder extends MailDocumentHandler  {

    private static final String[] TARGET_FOLDER_PATH = new String[] { MailConstants.E_SEARCH, MailConstants.A_FOLDER };
    private static final String[] RESPONSE_ITEM_PATH = new String[] { };
    protected String[] getProxiedIdPath(Element request)     { return TARGET_FOLDER_PATH; }
    protected boolean checkMountpointProxy(Element request)  { return true; }
    protected String[] getResponseItemPath()  { return RESPONSE_ITEM_PATH; }

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        ItemIdFormatter ifmt = new ItemIdFormatter(zsc);

        Element t = request.getElement(MailConstants.E_SEARCH);
        String name      = t.getAttribute(MailConstants.A_NAME);
        String query     = t.getAttribute(MailConstants.A_QUERY);
        String types     = t.getAttribute(MailConstants.A_SEARCH_TYPES, null);
        String sort      = t.getAttribute(MailConstants.A_SORTBY, null);
        String flags     = t.getAttribute(MailConstants.A_FLAGS, null);
        byte color       = (byte) t.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        String rgb       = t.getAttribute(MailConstants.A_RGB, null);

        MailItem.Color itemColor = rgb != null ? new MailItem.Color(rgb) : new MailItem.Color(color);
        ItemId iidParent = new ItemId(t.getAttribute(MailConstants.A_FOLDER), zsc);

        SearchFolder search = mbox.createSearchFolder(octxt, iidParent.getId(),
            name, query, types, sort, Flag.flagsToBitmask(flags), itemColor);

        Element response = zsc.createElement(MailConstants.CREATE_SEARCH_FOLDER_RESPONSE);
        if (search != null)
            ToXML.encodeSearchFolder(response, ifmt, search);
        return response;
    }
}
