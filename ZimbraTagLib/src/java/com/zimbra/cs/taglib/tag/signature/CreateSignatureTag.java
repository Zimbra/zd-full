/*
 * 
 */
package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateSignatureTag extends ZimbraSimpleTag {

    private String mName;
    private String mVar;
    private String mValue;
    private String mType = "text/plain";
    
    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }
    public void setVar(String var) { mVar = var; }
    public void setType(String type) { mType = type; }
    
    public void doTag() throws JspException, IOException {
        try {

            ZSignature sig = new ZSignature(mName, mValue);
            sig.setType(mType);
            
            String id = getMailbox().createSignature(sig);
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
