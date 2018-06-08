/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.zimbra.cs.offline.util.Xml;

public class SuccessResult extends Result {
    private AddAction addAction;
    private int cid = -1;
    private int catid = -1;

    public static final String TAG = "success";
    
    private static final String CID = "cid";
    private static final String CATID = "catid";

    private SuccessResult() {}

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

    public boolean isRemoved() {
        return addAction == null;
    }
    
    public AddAction getAddAction() {
        return addAction;
    }
    
    public int getContactId() {
        return cid;
    }

    public int getCategoryId() {
        return catid;
    }
    
    public static SuccessResult fromXml(Element e) {
        return new SuccessResult().parseXml(e);
    }

    private SuccessResult parseXml(Element e) {
        assert e.getTagName().equals(TAG);
        addAction = AddAction.fromXml(e);
        cid = Xml.getIntAttribute(e, CID);
        catid = Xml.getIntAttribute(e, CATID);
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG);
        if (addAction != null) addAction.setAttribute(e);
        if (cid != -1) e.setAttribute(CID, String.valueOf(cid));
        if (catid != -1) e.setAttribute(CATID, String.valueOf(catid));
        return e;
    }
}
