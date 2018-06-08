/*
 * 
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class TagContactTag extends ZimbraSimpleTag {

    private String mId;
    private String mTagid;
    private String mVar;
    private boolean mTag;

    public void setVar(String var) { mVar = var; }
    public void setId(String id) { mId = id; }
    public void setTag(boolean tag) { mTag = tag; }
    public void setTagid(String tagid) { mTagid = tagid; }

    public void doTag() throws JspException {
        try {
            ZMailbox.ZActionResult result = getMailbox().tagContact(mId, mTagid, mTag);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
