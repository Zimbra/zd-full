/*
 * 
 */
package com.zimbra.cs.index;

import com.zimbra.common.soap.Element;

public class ProxiedQueryInfo implements QueryInfo {

    private Element mElt;

    ProxiedQueryInfo(Element e) {
        mElt = e;
        mElt.detach();
    }

    public Element toXml(Element parent) {
        parent.addElement(mElt);
        return mElt;
    }

    @Override
    public String toString() {
        return mElt.toString();
    }

}
