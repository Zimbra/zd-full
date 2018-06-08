/*
 * 
 */
package com.zimbra.cs.taglib.tag.msg;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class TagMessageTag extends ZimbraSimpleTag {

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
            ZActionResult result = getMailbox().tagMessage(mId, mTagid, mTag);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
