/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZMailbox;

public abstract class ZimbraSimpleTag extends SimpleTagSupport {

    public ZMailbox getMailbox() throws JspException {
        return ZJspSession.getZMailbox((PageContext) getJspContext());
    }

}
