/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

public abstract class Entity {
    public abstract Element toXml(Document doc);

    @Override
    public String toString() {
        Element e = toXml(Xml.newDocument());
        return e != null ? Xml.toString(e) : super.toString();
    }
}
