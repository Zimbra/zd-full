/*
 * 
 */

package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectTag extends ZimbraSimpleTag {
    private String mUrl;

    public void setUrl(String url) { mUrl = url; }

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        PageContext pageContext = (PageContext) ctxt;
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        response.sendRedirect(mUrl);
    }
}
