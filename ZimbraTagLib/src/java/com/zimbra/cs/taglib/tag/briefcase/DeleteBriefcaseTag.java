/*
 * 
 */
package com.zimbra.cs.taglib.tag.briefcase;


import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class DeleteBriefcaseTag extends ZimbraSimpleTag {

    private String mId;
    private String mVar;

    public void setId(String id) { mId = id; }
    public void setVar(String var) { mVar = var; }

    public void doTag() throws JspException {
        try {
            ZActionResult result = getMailbox().deleteItem(mId, null);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}