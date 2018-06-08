/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.ZJspSession;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

import com.zimbra.common.auth.ZAuthToken;

public class LogoutTag extends ZimbraSimpleTag {
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pageContext = (PageContext) jctxt;
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse(); 
        ZAuthToken.clearCookies(response);
        ZJspSession.clearSession((PageContext)jctxt);
    }
}
