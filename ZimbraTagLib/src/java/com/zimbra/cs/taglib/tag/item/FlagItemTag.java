/*
 * 
 */
package com.zimbra.cs.taglib.tag.item;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZActionResultBean;
import com.zimbra.cs.zclient.ZMailbox.ZActionResult;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;

public class FlagItemTag extends ZimbraSimpleTag {

    private String mTc;
    private String mId;
    private boolean mFlag;
    private String mVar;

    public void setTc(String tc) { this.mTc = tc; }
    public void setId(String id) { this.mId = id; }
    public void setFlag(boolean flag) { this.mFlag = flag; }
    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException {
        try {
            ZActionResult result = getMailbox().flagItem(mId, mFlag, mTc);
            getJspContext().setAttribute(mVar, new ZActionResultBean(result), PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
