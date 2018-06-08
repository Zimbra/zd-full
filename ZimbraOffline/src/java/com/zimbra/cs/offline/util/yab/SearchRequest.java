/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchRequest extends Request {
    private static final String ACTION = "searchContacts";
    
    public SearchRequest(Session session) {
        super(session);
    }

    @Override
    protected String getAction() {
        return ACTION;
    }

    @Override
    protected Response parseResponse(Document doc) {
        return SearchResponse.fromXml(doc.getDocumentElement());
    }

    @Override
    public Element toXml(Document doc) {
        return null; // Not an XML entity
    }
}
