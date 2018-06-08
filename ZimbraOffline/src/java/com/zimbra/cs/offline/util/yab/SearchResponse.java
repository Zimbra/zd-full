/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;

import com.zimbra.cs.offline.util.Xml;

public class SearchResponse extends Response {
    private List<Contact> contacts;

    private static final String TAG = "search-response";

    private SearchResponse() {
        contacts = new ArrayList<Contact>();
    }

    public List<Contact> getContacts() {
        return contacts;
    }
    
    public static SearchResponse fromXml(Element e) {
        return new SearchResponse().parseXml(e);
    }

    private SearchResponse parseXml(Element e) {
        if (!e.getTagName().equals(TAG)) {
            throw new IllegalArgumentException(
                "Not a '" + TAG + "' element: " + e.getTagName());
        }
        List<Element> children = Xml.getChildren(e);
        for (Element child : children) {
            contacts.add(Contact.fromXml(child));
        }
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG);
        for (Contact contact : contacts) {
            e.appendChild(contact.toXml(doc));
        }
        return e;
    }
}
