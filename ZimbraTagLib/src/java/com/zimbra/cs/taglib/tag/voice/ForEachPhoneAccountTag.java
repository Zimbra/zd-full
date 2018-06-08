/*
 * 
 */
package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZPhoneAccountBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.util.List;

public class ForEachPhoneAccountTag extends ZimbraSimpleTag {
    
    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspFragment body = getJspBody();
        if (body == null) return;

        try {
            ZMailbox mbox = getMailbox();
            JspContext jctxt = getJspContext();
            List<ZPhoneAccount> accounts = mbox.getAllPhoneAccounts();
            for (ZPhoneAccount account : accounts) {
                jctxt.setAttribute(mVar, new ZPhoneAccountBean(account));
                body.invoke(null);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }    
}