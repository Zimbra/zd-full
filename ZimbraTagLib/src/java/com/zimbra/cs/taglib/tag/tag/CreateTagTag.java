/*
 * 
 */
package com.zimbra.cs.taglib.tag.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class CreateTagTag extends ZimbraSimpleTag {

    private String mName;
    private String mVar;
    private ZTag.Color mColor;

    public void setName(String name) { mName = name; }
    public void setVar(String var) { mVar = var; }
    public void setColor(String color) throws ServiceException { mColor = ZTag.Color.fromString(color); }

    public void doTag() throws JspException, IOException {
        try {
            String id = getMailbox().createTag(mName, mColor).getId();
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
