/*
 * 
 */
package com.zimbra.cs.dav.service.method;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.DavProtocol;
import com.zimbra.cs.dav.resource.Collection;
import com.zimbra.cs.dav.resource.DavResource;
import com.zimbra.cs.dav.resource.MailItemResource;
import com.zimbra.cs.dav.resource.Notebook;
import com.zimbra.cs.dav.resource.UrlNamespace;
import com.zimbra.cs.dav.service.DavMethod;

public class Move extends DavMethod {
    public static final String MOVE  = "MOVE";
    public String getName() {
        return MOVE;
    }

    public void handle(DavContext ctxt) throws DavException, IOException, ServiceException {
        DavResource rs = ctxt.getRequestedResource();
        if (!(rs instanceof MailItemResource))
            throw new DavException("cannot move", HttpServletResponse.SC_BAD_REQUEST, null);
        Collection col = getDestinationCollection(ctxt);
        MailItemResource mir = (MailItemResource) rs;
        if (ctxt.isOverwriteSet()) {
            mir.moveWithOverwrite(ctxt, col);
        } else {
            mir.move(ctxt, col);
        }

        renameIfNecessary(ctxt, mir, col);
        ctxt.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    protected void renameIfNecessary(DavContext ctxt, DavResource rs, MailItemResource destCollection) throws DavException {
        if (!(rs instanceof Collection) && !(rs instanceof Notebook))
            return;
        String oldName = ctxt.getItem();
        String dest = getDestination(ctxt);
        int begin, end;
        end = dest.length();
        if (dest.endsWith("/"))
            end--;
        begin = dest.lastIndexOf("/", end-1);
        String newName = dest.substring(begin+1, end);
        try {
            newName = URLDecoder.decode(newName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ZimbraLog.dav.warn("can't decode URL ", dest, e);
        }
        if (!oldName.equals(newName))
            rs.rename(ctxt, newName, destCollection);
    }

    protected String getDestination(DavContext ctxt) throws DavException {
        String destination = ctxt.getRequest().getHeader(DavProtocol.HEADER_DESTINATION);
        if (destination == null)
            throw new DavException("no destination specified", HttpServletResponse.SC_BAD_REQUEST, null);
        return destination;
    }
    protected Collection getDestinationCollection(DavContext ctxt) throws DavException {
        String destinationUrl = getDestination(ctxt);
        try {
            destinationUrl = URLDecoder.decode(destinationUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ZimbraLog.dav.warn("can't decode destination url %s", destinationUrl, e);
        }
        if (!destinationUrl.endsWith("/")) {
            int slash = destinationUrl.lastIndexOf('/');
            destinationUrl = destinationUrl.substring(0, slash+1);
        }
        try {
            DavResource r = UrlNamespace.getResourceAtUrl(ctxt, destinationUrl);
            if (r instanceof Collection)
                return ((Collection)r);
            return UrlNamespace.getCollectionAtUrl(ctxt, destinationUrl);
        } catch (Exception e) {
            throw new DavException("can't get destination collection", DavProtocol.STATUS_FAILED_DEPENDENCY);
        }
    }
}
