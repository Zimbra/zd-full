/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;

public class AddRequest extends Request {
    private final List<Contact> contacts;

    public static final String ACTION = "addContacts";
    public static final String TAG = "add-request";
    
    public AddRequest(Session session) {
        super(session);
        contacts = new ArrayList<Contact>();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    public Element toXml(Document doc) {
        if (contacts.size() == 0) {
            throw new IllegalStateException(
                "AddRequest must contain at least one contact");
        }
        Element e = doc.createElement(TAG);
        for (Contact contact : contacts) {
            e.appendChild(contact.toXml(doc));
        }
        return e;
    }

    @Override
    protected Response parseResponse(Document doc) {
        return AddResponse.fromXml(doc.getDocumentElement());
    }
}
