/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.ZJspSession;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class GetMailboxTag extends ZimbraSimpleTag {
    
    private String mVar;
    private boolean mRefreshAccount;
    private String mRestAuthToken;
    private ZAuthToken mRestAuthTokenObject;
    private String mRestTargetAccountId;
    
    public void setVar(String var) { this.mVar = var; }
    public void setRefreshaccount(boolean refresh) { this.mRefreshAccount = refresh; }
    public void setRestauthtoken(String authToken) { this.mRestAuthToken = authToken; }
    public void setRestauthtokenobject(ZAuthToken authToken) { this.mRestAuthTokenObject = authToken; }
    public void setResttargetaccountid(String targetId) { this.mRestTargetAccountId = targetId; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext ctxt = getJspContext();
            
            if (mRestAuthTokenObject != null) {
                ctxt.setAttribute(mVar, new ZMailboxBean(ZJspSession.getRestMailbox((PageContext)ctxt, mRestAuthTokenObject, mRestTargetAccountId)),  PageContext.REQUEST_SCOPE);
            } else if (mRestAuthToken != null && mRestAuthToken.length() > 0) {
                ctxt.setAttribute(mVar, new ZMailboxBean(ZJspSession.getRestMailbox((PageContext)ctxt, mRestAuthToken, mRestTargetAccountId)),  PageContext.REQUEST_SCOPE);
            } else {
                ZMailboxBean bean = (ZMailboxBean) ctxt.getAttribute(mVar, PageContext.REQUEST_SCOPE);
                if ( bean == null) {
                    bean = new ZMailboxBean(getMailbox());
                    ctxt.setAttribute(mVar, bean,  PageContext.REQUEST_SCOPE);
                }
                if (mRefreshAccount)
                    bean.getAccountInfoReload();
            }
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
