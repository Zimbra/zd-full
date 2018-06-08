/*
 * 
 */
package com.zimbra.zimbrasync.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OperationContext;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

public class MailboxContactAppData extends MailboxAppData {
    private ContactAppData appData;
    private ProtocolVersion protocolVersion = new ProtocolVersion("2.5");
    
    public MailboxContactAppData() {
        appData = new ContactAppData();
    }
    
    public MailboxContactAppData(ContactAppData appData) {
        this.appData = appData;
    }
    
    public MailboxContactAppData(OperationContext octxt, Contact contact, ProtocolVersion version) throws ServiceException, IOException {
        protocolVersion = version;
        appData = new ContactAppData();
        appData.protocolVersion = protocolVersion;
        ParsedContact pc = new ParsedContact(contact);
        appData.attrs = pc.getFields();
        setAttachments(pc.getAttachments());
        addCategories(octxt, contact.getMailbox(), contact);
    }
    
    @Override
    AppData getAppData() {
        return appData;
    }
    
    public ContactAppData getContactAppData() {
        return appData;
    }
    
    public void parse(BinaryParser parser) throws BinaryCodecException, IOException {
        appData.parse(parser);
        Contact.normalizeFileAs(appData.attrs); //Special treatment for FileAs
    }
    
    public void encode(BinarySerializer serializer, boolean useCategories, boolean isEmptyElementOK, boolean useRtf, int truncationSize, boolean allOrNone)
            throws BinaryCodecException, IOException {
        appData.encode(serializer, useCategories, isEmptyElementOK, useRtf, truncationSize, allOrNone);
    }
    
    private void setAttachments(List<Attachment> attachments) throws IOException {
        if (attachments != null && attachments.size() > 0) {
            for (Attachment attach : attachments) {
                if (attach.getContentType().startsWith("image/jpeg") && attach.getSize() > 0 && attach.getSize() <= LC.zimbra_activesync_contact_image_size.intValue()) {
                    appData.image = attach.getContent();
                    break;
                }
            }
        }
    }
    
    private List<Attachment> getAttachments() {
        if (appData.image == null)
            return null;
        List<Attachment> attachments = new ArrayList<Attachment>(1);
        attachments.add(new Attachment(appData.image, "image/jpeg", ContactConstants.A_image, "contact.jpg"));
        return attachments;
    }
    
    public Contact createContact(Mailbox mbox, OperationContext octxt, int folderId) throws ServiceException {
        ParsedContact pc = new ParsedContact(appData.attrs, getAttachments());
        Contact contact = mbox.createContact(octxt, pc, folderId, null);
        saveMailItemCategories(octxt, mbox, contact.getId(), MailItem.TYPE_CONTACT);
        return contact;
    }
    
    public Contact modifyContact(Mailbox mbox, OperationContext octxt, int id) throws ServiceException {
        ParsedContact old = new ParsedContact(mbox.getContactById(octxt, id));
        if (!appData.attrs.containsKey(ContactConstants.A_notes) && old.getFields().containsKey(ContactConstants.A_notes))
            appData.attrs.put(ContactConstants.A_notes, old.getFields().get(ContactConstants.A_notes));
        else if (appData.attrs.containsKey(ContactConstants.A_notes) && appData.attrs.get(ContactConstants.A_notes).length() == 0) //means client to remove notes
            appData.attrs.remove(ContactConstants.A_notes);
        
        List<Attachment> attachments = getAttachments();
        if (attachments == null && !appData.isImageCleared && old.hasAttachment())
            attachments = old.getAttachments();
        ParsedContact pc = new ParsedContact(appData.attrs, attachments);
        mbox.modifyContact(octxt, id, pc);
        saveMailItemCategories(octxt, mbox, id, MailItem.TYPE_CONTACT);
        return mbox.getContactById(octxt, id);
    }
}
