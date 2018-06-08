/*
 * 
 */
package com.zimbra.cs.client.soap;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.zimbra.common.soap.DomUtil;
import com.zimbra.common.soap.MailConstants;

public class LmcWikiActionRequest extends LmcItemActionRequest {
    private String mName;

    public void setName(String n) { mName = n; }
    public String getName() { return mName; }

    protected Element getRequestXML() {
        Element request = DocumentHelper.createElement(MailConstants.WIKI_ACTION_REQUEST);
        Element a = DomUtil.add(request, MailConstants.E_ACTION, "");
        DomUtil.addAttr(a, MailConstants.A_ID, mIDList);
        DomUtil.addAttr(a, MailConstants.A_OPERATION, mOp);
        DomUtil.addAttr(a, MailConstants.A_TAG, mTag);
        DomUtil.addAttr(a, MailConstants.A_FOLDER, mFolder);
        DomUtil.addAttr(a, MailConstants.A_NAME, mName);
        return request;
    }
}
