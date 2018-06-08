/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.taglib.bean.ZTagLibException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class CheckCrumbTag extends ZimbraSimpleTag {

    private String mCrumb;

    public void setCrumb(String crumb) { mCrumb = crumb; }

    public void doTag() throws JspException, IOException {
        try {
            ZMailbox mbox = getMailbox();
            String validCrumb = mbox.getAccountInfo(false).getCrumb();
            if (validCrumb == null || !validCrumb.equals(mCrumb))
                throw ZTagLibException.INVALID_CRUMB("missing valid crumb", null);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
