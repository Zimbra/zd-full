/*
 * 
 */
package com.zimbra.cs.offline.util.yc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class FieldValue extends Entity {

    private static final String TAG_NAME = "value";
    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG_NAME);
        appendValues(e);
        return e;
    }
    
    protected abstract void appendValues(Element parent);

    public abstract Fields.Type getType();
}
