/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;

import com.zimbra.cs.taglib.bean.ZExceptionBean;
import com.zimbra.cs.taglib.bean.ZTagLibException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.common.service.ServiceException;
import org.mortbay.io.RuntimeIOException;

public class GetExceptionTag extends ZimbraSimpleTag {
    
    private String mVar;
    private Exception mException;
    
    public void setVar(String var) { this.mVar = var; }
    
    public void setException(Exception e) { this.mException = e; }
    
    public void doTag() throws JspException, IOException {
        ZExceptionBean eb = new ZExceptionBean(mException);
        Exception e = eb.getException();
        if (e != null) {
            if (
                    (!(e instanceof ServiceException)) ||
                            ((e instanceof ZTagLibException) && (!(e.getCause() instanceof SkipPageException || e.getCause() instanceof IllegalStateException || e.getCause() instanceof RuntimeIOException))) || (e instanceof ZClientException))
                ZimbraLog.webclient.warn("local exception", e);
        }
        getJspContext().setAttribute(mVar, eb,  PageContext.PAGE_SCOPE);
    }
}
