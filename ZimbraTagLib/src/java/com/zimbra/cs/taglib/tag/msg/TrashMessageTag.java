/*
 * 
 */

package com.zimbra.cs.taglib.tag.msg;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class TrashMessageTag extends ZimbraSimpleTag {

    private String mId;
    private String mVar;

    public void setId(String id) { mId = id; }
    public void setVar(String var) { mVar = var; }

    public void doTag() throws JspException {
        try {
            ZActionResult result = getMailbox().trashMessage(mId);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
