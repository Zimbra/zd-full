/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class ContactResult extends Result {
    private AddAction addAction;
    private Contact contact;

    public static final String TAG = "contact";

    private ContactResult() {}

    @Override
    public boolean isError() {
        return false;
    }
    
    public boolean isAdded() {
        return addAction == AddAction.ADD;
    }

    public boolean isMerged() {
        return addAction == AddAction.MERGE;
    }

    public Contact getContact() {
        return contact;
    }
    
    public static ContactResult fromXml(Element e) {
        return new ContactResult().parseXml(e);
    }

    private ContactResult parseXml(Element e) {
        assert e.getTagName().equals(TAG);
        addAction = AddAction.fromXml(e);
        if (addAction == null) {
            throw new IllegalArgumentException("Missing add-action element");
        }
        contact = Contact.fromXml(e);
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = contact.toXml(doc, TAG);
        addAction.setAttribute(e);
        return e;
    }
}
