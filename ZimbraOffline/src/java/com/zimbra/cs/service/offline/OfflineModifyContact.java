/*
 * 
 */
package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.service.mail.ModifyContact;
import com.zimbra.cs.service.util.ItemId;

public class OfflineModifyContact extends ModifyContact {

    @Override
    protected Element proxyRequest(Element request, Map<String, Object> context, ItemId iidRequested, ItemId iidResolved)
            throws ServiceException {
        OfflineDocumentHandlers.uploadAttachmentToRemoteServer(request, iidRequested, iidResolved);
        return super.proxyRequest(request, context, iidRequested, iidResolved);
    }
}
