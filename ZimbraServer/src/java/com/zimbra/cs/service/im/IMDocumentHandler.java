/*
 * 
 */
package com.zimbra.cs.service.im;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.im.IMPersona;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public abstract class IMDocumentHandler extends DocumentHandler {

    protected IMPersona getRequestedPersona(ZimbraSoapContext zsc) throws ServiceException {
        return getRequestedMailbox(zsc).getPersona();
    }
}
