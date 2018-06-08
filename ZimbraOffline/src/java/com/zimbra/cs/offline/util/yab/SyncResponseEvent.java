/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

public class SyncResponseEvent extends Entity {
    private Type type;
    private Contact contact;
    private int lastModifiedTime = -1;

    private static final String LMT = "lmt";

    public enum Type {
        ADDRESS_BOOK_RESET("address-book-reset"),
        ADD_CONTACT("add-contact"),
        UPDATE_CONTACT("update-contact"),
        REMOVE_CONTACT("remove-contact");

        String tag;
        Type(String tag) { this.tag = tag; }

        static Type fromXml(Element e) {
            String tag = e.getTagName();
            if (tag.equals(ADDRESS_BOOK_RESET.tag)) {
                return ADDRESS_BOOK_RESET;
            } else if (tag.equals(ADD_CONTACT.tag)) {
                return ADD_CONTACT;
            } else if (tag.equals(UPDATE_CONTACT.tag)) {
                return UPDATE_CONTACT;
            } else if (tag.equals(REMOVE_CONTACT.tag)) {
                return REMOVE_CONTACT;
            } else {
                throw new IllegalArgumentException(
                    "Not a valid response event type: " + tag);
            }
        }
    }
    
    private SyncResponseEvent() {}

    public Type getType() {
        return type;
    }

    public boolean isAddContact() {
        return type == Type.ADD_CONTACT;
    }

    public boolean isUpdateContact() {
        return type == Type.UPDATE_CONTACT;
    }

    public boolean isRemoveContact() {
        return type == Type.REMOVE_CONTACT;
    }

    public boolean isAddressBookReset() {
        return type == Type.ADDRESS_BOOK_RESET;
    }

    public Contact getContact() {
        return contact;
    }

    public int getContactId() {
        return contact.getId();
    }
    
    public int getLastModifiedTime() {
        return lastModifiedTime;
    }

    public static SyncResponseEvent fromXml(Element e) {
        return new SyncResponseEvent().parseXml(e);
    }

    public SyncResponseEvent parseXml(Element e) {
        type = Type.fromXml(e);
        lastModifiedTime = Xml.getIntAttribute(e, LMT);
        switch (type) {
        case ADD_CONTACT: case UPDATE_CONTACT: case REMOVE_CONTACT:
            contact = Contact.fromXml(e);
        }
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e;
        if (contact != null) {
            e = contact.toXml(doc, type.tag);
        } else {
            e = doc.createElement(type.tag);
        }
        if (lastModifiedTime != -1) {
            e.setAttribute(LMT, String.valueOf(lastModifiedTime));
        }
        return e;
    }
}
