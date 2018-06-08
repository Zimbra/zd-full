/*
 * 
 */
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZContactBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetContactTag extends ZimbraSimpleTag {
    
    private String mVar;
    private String mId;
    private boolean mSync;
    
    public void setVar(String var) { this.mVar = var; }
    public void setId(String id) { this.mId = id; }    
    public void setSync(boolean sync) { this.mSync = sync; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            jctxt.setAttribute(mVar, new ZContactBean(mbox.getContact(mId)),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
