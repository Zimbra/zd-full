/*
 * 
 */
package com.zimbra.cs.dav.service.method;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.resource.Collection;
import com.zimbra.cs.dav.resource.DavResource;
import com.zimbra.cs.dav.resource.MailItemResource;

public class Copy extends Move {
    public static final String COPY  = "COPY";
    public String getName() {
        return COPY;
    }

    public void handle(DavContext ctxt) throws DavException, IOException, ServiceException {
        DavResource rs = ctxt.getRequestedResource();
        if (!(rs instanceof MailItemResource))
            throw new DavException("cannot copy", HttpServletResponse.SC_BAD_REQUEST, null);
        Collection col = getDestinationCollection(ctxt);
        MailItemResource mir = (MailItemResource) rs;
        DavResource copy;
        if (ctxt.isOverwriteSet()) {
            copy = mir.copyWithOverwrite(ctxt, col);
        } else {
            copy = mir.copy(ctxt, col);
        }

        renameIfNecessary(ctxt, copy, col);
        ctxt.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
