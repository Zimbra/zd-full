/*
 * 
 */
package com.zimbra.cs.taglib.tag.conv;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class MarkConversationReadTag extends ZimbraSimpleTag {

    private String mTc;
    private String mId;
    private boolean mRead;
    private String mVar;

    public void setVar(String var) { this.mVar = var; }
    public void setTc(String tc) { this.mTc = tc; }
    public void setId(String id) { this.mId = id; }
    public void setRead(boolean read) { this.mRead = read; }

    public void doTag() throws JspException {
        try {
            ZActionResult result = getMailbox().markConversationRead(mId, mRead, mTc);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
