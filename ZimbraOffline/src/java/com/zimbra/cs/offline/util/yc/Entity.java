/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

public abstract class Entity {
    
    public Element toXml(Document doc) {
        throw new UnsupportedOperationException();
    }

    public void extractFromXml(Element e) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        Element e = toXml(Xml.newDocument());
        return e != null ? Xml.toString(e) : super.toString();
    }
}
