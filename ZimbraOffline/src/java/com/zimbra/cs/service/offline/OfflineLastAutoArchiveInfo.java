package com.zimbra.cs.service.offline;

import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.mailbox.AutoArchive;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class OfflineLastAutoArchiveInfo extends DocumentHandler {
    @Override
    public Element handle(Element request, Map<String, Object> context)
            throws ServiceException {
        long lastAutoArchiveTimeInMillis = Long.parseLong(AutoArchive.getLastAutoArchiveTimestamp());
        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(OfflineConstants.GET_LAST_AUTO_ARCHIVE_INFO_RESPONSE);

        response.addAttribute(OfflineConstants.A_offlineLastAutoArchive, lastAutoArchiveTimeInMillis);
        return response;
    }
}
