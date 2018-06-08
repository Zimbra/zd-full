/*
 * 
 */
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;

public enum AddAction {
    ADD("add"), MERGE("merge");

    private static final String TAG = "add-action";
    
    private String attr;

    private AddAction(String addr) { this.attr = addr; }

    public static AddAction fromXml(Element e) {
        String s = e.getAttribute(TAG);
        if (s == null || s.equals("")) return null;
        if (ADD.attr.equals(s)) return ADD;
        if (MERGE.attr.equals(s)) return MERGE;
        throw new IllegalArgumentException("Invalid 'add-action' value: " + s);
    }

    public void setAttribute(Element e) {
        e.setAttribute(TAG, attr);
    }
}
