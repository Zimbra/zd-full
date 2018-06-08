/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZTagBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZTag;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.List;

public class ForEachTagTag extends ZimbraSimpleTag {
    
    private String mVar;
    
    public void setVar(String var) { this.mVar = var; }
    
    public void doTag() throws JspException, IOException {
        try {
            JspFragment body = getJspBody();
            if (body == null) return;
            JspContext jctxt = getJspContext();
            ZMailbox mbox = getMailbox();
            List<ZTag> tags = mbox.getAllTags();
            for (ZTag tag: tags) {
                jctxt.setAttribute(mVar, new ZTagBean(tag));
                body.invoke(null);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
