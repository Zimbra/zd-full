/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZComposeUploaderBean;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ComposeUploaderTag extends ZimbraSimpleTag {

    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        ZComposeUploaderBean compose = (ZComposeUploaderBean) jctxt.getAttribute(mVar, PageContext.REQUEST_SCOPE);
        if (compose == null) {
            try {
                jctxt.setAttribute(mVar, new ZComposeUploaderBean(pc, getMailbox()), PageContext.REQUEST_SCOPE);
            } catch (ServiceException e) {
                throw new JspTagException("compose upload failed", e);
            }
        }
    }
}
