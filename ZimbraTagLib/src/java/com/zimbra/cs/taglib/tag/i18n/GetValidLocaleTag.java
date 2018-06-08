/*
 * 
 */
package com.zimbra.cs.taglib.tag.i18n;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;


public class GetValidLocaleTag extends ZimbraSimpleTag {


	private String mVar;
    private String mLocale;
	
    public void setVar(String var) { this.mVar = var; }
	public void setLocale(String locale) { this.mLocale = locale; }


    // simple tag methods

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        ZMailbox mbox = getMailbox();
        try {
            boolean isValid = false;
            List<String> locales = mbox.getAvailableLocales();
            for(String s : locales) {
                if (s.equalsIgnoreCase(this.mLocale)) {
                    isValid = true;
                }
                if (isValid) {
                    continue;
                }
            }
            ctxt.setAttribute(mVar, isValid,  PageContext.REQUEST_SCOPE);
        }
        catch(ServiceException e) {
            throw new JspTagException(e.getMessage(), e);   
        }
    }

}

