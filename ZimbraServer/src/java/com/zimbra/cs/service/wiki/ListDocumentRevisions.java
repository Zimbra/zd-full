/*
 * 
 */

package com.zimbra.cs.service.wiki;

import java.util.Map;

import com.zimbra.cs.mailbox.Document;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.service.mail.ToXML;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;

public class ListDocumentRevisions extends WikiDocumentHandler {

	@Override
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Mailbox mbox = getRequestedMailbox(zsc);
        OperationContext octxt = getOperationContext(zsc, context);
        ItemIdFormatter ifmt = new ItemIdFormatter(zsc);

        Element doc = request.getElement(MailConstants.E_DOC);
        String id = doc.getAttribute(MailConstants.A_ID);
        int version = (int) doc.getAttributeLong(MailConstants.A_VERSION, -1);
        int count = (int) doc.getAttributeLong(MailConstants.A_COUNT, 1);

        Element response = zsc.createElement(MailConstants.LIST_DOCUMENT_REVISIONS_RESPONSE);

        Document item;

        ItemId iid = new ItemId(id, zsc);
        item = mbox.getDocumentById(octxt, iid.getId());
        
        byte view = mbox.getFolderById(octxt, item.getFolderId()).getDefaultView();
        if (view == MailItem.TYPE_WIKI)
    		checkNotebookEnabled(zsc);
        else if (view == MailItem.TYPE_DOCUMENT)
    		checkBriefcaseEnabled(zsc);

        if (version < 0)
        	version = item.getVersion();
        byte type = item.getType();
        while (version > 0 && count > 0) {
        	item = (Document) mbox.getItemRevision(octxt, iid.getId(), type, version);
        	if (item != null)
        	    ToXML.encodeDocument(response, ifmt, octxt, item);
        	version--; count--;
        }

        return response;
	}
}
