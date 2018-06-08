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

public class FlagMessageTag extends ZimbraSimpleTag {

    private String mId;
    private boolean mFlag;
    private String mVar;

    public void setId(String id) { this.mId = id; }
    public void setFlag(boolean flag) { this.mFlag = flag; }
    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException {
        try {
            ZActionResult result = getMailbox().flagMessage(mId, mFlag);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
