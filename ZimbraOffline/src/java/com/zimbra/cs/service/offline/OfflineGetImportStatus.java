/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.ZcsMailbox;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.service.mail.GetImportStatus;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineGetImportStatus extends GetImportStatus {

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException, SoapFaultException {
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Mailbox mbox = getRequestedMailbox(zsc);
        if (!(mbox instanceof ZcsMailbox))
            throw OfflineServiceException.MISCONFIGURED("incorrect mailbox class: " + mbox.getClass().getSimpleName());

        // proxy this operation to the remote server
        Element response = ((ZcsMailbox) mbox).sendRequest(request);
        return response;
    }
}
