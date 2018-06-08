/*
 * 
 */
package com.zimbra.cs.taglib.tag.briefcase;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.bean.ZDocumentBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZDocument;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;

public class GetDocumentTag extends ZimbraSimpleTag {

    private String mVar;
    private String mId;
    private ZMailboxBean mMailbox;

    public void setId(String id) { this.mId = id; }
    public void setVar(String var) { this.mVar = var; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }    

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();
			ZDocument doc = mbox.getDocument(this.mId);
            jctxt.setAttribute(mVar, new ZDocumentBean(doc), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }

    }

}
