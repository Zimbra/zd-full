/*
 * 
 */
package com.zimbra.cs.taglib.tag.msg;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.util.List;

public class SaveAttachmentsToBriefcaseTag extends ZimbraSimpleTag {

    private String mId;
    private String[] mPartId;
    private String mFolderId;
    private String mVar;
    
    public void setVar(String var) { this.mVar = var; }
    public void setMid(String mId) { this.mId = mId; }
    public void setPartId(String[] partId) { this.mPartId  = partId; }
    public void setFolderId(String folderId) { this.mFolderId = folderId; }

    public void doTag() throws JspException {
        try {
            List<String> result = getMailbox().saveAttachmentsToBriefcase(mId,mPartId,mFolderId);
            getJspContext().setAttribute(mVar, result, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}