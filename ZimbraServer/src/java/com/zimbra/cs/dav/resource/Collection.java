/*
 * 
 */
package com.zimbra.cs.dav.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.cs.dav.DavContext;
import com.zimbra.cs.dav.DavElements;
import com.zimbra.cs.dav.DavException;
import com.zimbra.cs.dav.DavProtocol;
import com.zimbra.cs.dav.property.Acl;
import com.zimbra.cs.mailbox.Document;
import com.zimbra.cs.mailbox.Folder;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.service.FileUploadServlet;

/**
 * RFC 2518bis section 5.
 * 
 * Collections map to mailbox folders.
 *
 */
public class Collection extends MailItemResource {

    protected byte mView;
    protected byte mType;
    protected int mMailboxId;

    public Collection(DavContext ctxt, Folder f) throws DavException, ServiceException {
        super(ctxt, f);
        setCreationDate(f.getDate());
        setLastModifiedDate(f.getChangeDate());
        setProperty(DavElements.P_DISPLAYNAME, f.getName());
        setProperty(DavElements.P_GETCONTENTLENGTH, "0");
        mId = f.getId();
        mView = f.getDefaultView();
        mType = f.getType();
        addProperties(Acl.getAclProperties(this, f));
        mMailboxId = f.getMailboxId();
    }

    public Collection(String name, String acct) throws DavException {
        super(name, acct);
        long now = System.currentTimeMillis();
        setCreationDate(now);
        setLastModifiedDate(now);
        setProperty(DavElements.P_DISPLAYNAME, name.substring(1));
        setProperty(DavElements.P_GETCONTENTLENGTH, "0");
        try {
            addProperties(Acl.getAclProperties(this, null));
        } catch (ServiceException se) {
        }
    }

    @Override
    public String getContentType(DavContext ctxt) {
        if (ctxt.isWebRequest())
            return MimeConstants.CT_TEXT_PLAIN;
        return MimeConstants.CT_TEXT_XML;
    }
    
    @Override
    public boolean hasContent(DavContext ctxt) {
        return true;
    }
    
    @Override
    public InputStream getContent(DavContext ctxt) throws IOException, DavException {
        if (ctxt.isWebRequest())
            return new ByteArrayInputStream(getTextContent(ctxt).getBytes("UTF-8"));
        return new ByteArrayInputStream(getPropertiesAsText(ctxt).getBytes("UTF-8"));
    }
    
    @Override
    public boolean isCollection() {
        return true;
    }

    @Override
    public java.util.Collection<DavResource> getChildren(DavContext ctxt) throws DavException {
        ArrayList<DavResource> children = new ArrayList<DavResource>();

        try {
            ctxt.setCollectionPath(getUri());
            List<MailItem> items = getChildrenMailItem(ctxt);
            for (MailItem item : items) {
                DavResource rs = UrlNamespace.getResourceFromMailItem(ctxt, item);
                if (rs != null)
                    children.add(rs);
            }
        } catch (ServiceException e) {
            ZimbraLog.dav.error("can't get children from folder: id="+mId, e);
        }
        // this is where we add the phantom folder for attachment browser.
        if (isRootCollection()) {
            children.add(new Collection(UrlNamespace.ATTACHMENTS_PREFIX, getOwner()));
        }
        return children;
    }

    private List<MailItem> getChildrenMailItem(DavContext ctxt) throws DavException,ServiceException {
        Mailbox mbox = getMailbox(ctxt);

        List<MailItem> ret = new ArrayList<MailItem>();

        // XXX aggregate into single call
        ret.addAll(mbox.getItemList(ctxt.getOperationContext(), MailItem.TYPE_FOLDER, mId));
        ret.addAll(mbox.getItemList(ctxt.getOperationContext(), MailItem.TYPE_DOCUMENT, mId));
        ret.addAll(mbox.getItemList(ctxt.getOperationContext(), MailItem.TYPE_WIKI, mId));
        ret.addAll(mbox.getItemList(ctxt.getOperationContext(), MailItem.TYPE_CONTACT, mId));
        return ret;
    }

    public Collection mkCol(DavContext ctxt, String name) throws DavException {
        return mkCol(ctxt, name, mView);
    }

    public Collection mkCol(DavContext ctxt, String name, byte view) throws DavException {
        try {
            Mailbox mbox = getMailbox(ctxt);
            Folder f = mbox.createFolder(ctxt.getOperationContext(), name, mId, view, 0, (byte)0, null);
            return (Collection)UrlNamespace.getResourceFromMailItem(ctxt, f);
        } catch (ServiceException e) {
            if (e.getCode().equals(MailServiceException.ALREADY_EXISTS))
                throw new DavException("item already exists", HttpServletResponse.SC_CONFLICT, e);
            else if (e.getCode().equals(ServiceException.PERM_DENIED))
                throw new DavException("permission denied", HttpServletResponse.SC_FORBIDDEN, e);
            else
                throw new DavException("can't create", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    // create Document at the URI
    public DavResource createItem(DavContext ctxt, String name) throws DavException, IOException {
        Mailbox mbox = null;
        try {
            mbox = getMailbox(ctxt);
        } catch (ServiceException e) {
            throw new DavException("cannot get mailbox", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
        }

        FileUploadServlet.Upload upload = ctxt.getUpload();
        String ctype = upload.getContentType();
        if (ctype != null && 
                (ctype.startsWith(DavProtocol.VCARD_CONTENT_TYPE) ||
                 ctype.startsWith(MimeConstants.CT_TEXT_VCARD_LEGACY) ||
                 ctype.startsWith(MimeConstants.CT_TEXT_VCARD_LEGACY2))) {
            // create vcard if content type is text/vcard or legacy
            // vCard MIME types such as text/x-vcard or text/directory.
            return createVCard(ctxt, name);
        }
        String author = ctxt.getAuthAccount().getName();
        try {
            // add a revision if the resource already exists
            MailItem item = mbox.getItemByPath(ctxt.getOperationContext(), ctxt.getPath());
            if (item.getType() != MailItem.TYPE_DOCUMENT && item.getType() != MailItem.TYPE_WIKI)
                throw new DavException("no DAV resource for " + MailItem.getNameForType(item.getType()), HttpServletResponse.SC_NOT_ACCEPTABLE, null);
            Document doc = mbox.addDocumentRevision(ctxt.getOperationContext(), item.getId(), author, name, null, upload.getInputStream());
            return new Notebook(ctxt, doc);
        } catch (ServiceException e) {
            if (!(e instanceof NoSuchItemException))
                throw new DavException("cannot get item ", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
        }

        // create
        try {
            Document doc = mbox.createDocument(ctxt.getOperationContext(), mId, name, ctype, author, null, upload.getInputStream());
            Notebook notebook =  new Notebook(ctxt, doc);
            notebook.mNewlyCreated = true;
            return notebook;
        } catch (ServiceException se) {
            throw new DavException("cannot create ", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, se);
        }
    }
    
    protected DavResource createVCard(DavContext ctxt, String name) throws DavException, IOException {
        return AddressObject.create(ctxt, name, this);
    }
    
    @Override public void delete(DavContext ctxt) throws DavException {
        String user = ctxt.getUser();
        String path = ctxt.getPath();
        if (user == null || path == null)
            throw new DavException("invalid uri", HttpServletResponse.SC_NOT_FOUND, null);
        try {
            Mailbox mbox = getMailbox(ctxt);
            mbox.delete(ctxt.getOperationContext(), mId, mType);
        } catch (ServiceException e) {
            throw new DavException("cannot get item", HttpServletResponse.SC_NOT_FOUND, e);
        }
    }

    protected boolean isRootCollection() {
        return (mId == Mailbox.ID_FOLDER_USER_ROOT);
    }
}
