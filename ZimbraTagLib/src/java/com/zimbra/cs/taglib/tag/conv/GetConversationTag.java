/*
 * 
 */
package com.zimbra.cs.taglib.tag.conv;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZConversationBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZConversation;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.Fetch;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class GetConversationTag extends ZimbraSimpleTag {

    private String mVar;
    private String mId;
    private Fetch mFetch;

    public void setVar(String var) { this.mVar = var; }
    
    public void setFetch(String fetch) throws ServiceException { this.mFetch = Fetch.fromString(fetch); }    

    public void setId(String id) { this.mId = id; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            ZConversation conv = mbox.getConversation(mId, mFetch);
            jctxt.setAttribute(mVar, new ZConversationBean(conv),  PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
