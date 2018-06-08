/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

import java.util.List;

public class Bucket extends Entity {
    private int id;
    private int count;
    private Contact start;
    private Contact end;

    public static final String TAG = "bucket";

    private static final String ID = "id";
    private static final String CONTACT_COUNT = "contact-count";
    
    private Bucket() {}

    public int getId() { return id; }
    public int getContactCount() { return count; }
    public Contact getStartContact() { return start; }
    public Contact getEndContact() { return end; }

    public static Bucket fromXml(Element e) {
        return new Bucket().parseXml(e);
    }

    private Bucket parseXml(Element e) {
        if (!e.getTagName().equals(TAG)) {
            throw new IllegalArgumentException(
                "Not a '" + TAG + "' element: " + e.getTagName());
        }
        id = Xml.getIntAttribute(e, "id");
        count = Xml.getIntAttribute(e, "contact-count");
        List<Element> children = Xml.getChildren(e);
        if (children.size() != 2) {
            throw new IllegalArgumentException("Invalid '" + TAG + "' element");
        }
        start = Contact.fromXml(children.get(0));
        end = Contact.fromXml(children.get(1));
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG);
        Xml.appendElement(e, ID, String.valueOf(id));
        Xml.appendElement(e, CONTACT_COUNT, String.valueOf(count));
        e.appendChild(start.toXml(doc));
        e.appendChild(end.toXml(doc));
        return e;
    }
}
