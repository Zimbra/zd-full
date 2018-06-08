/*
 * 
 */

package com.zimbra.cs.taglib.tag.voice;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZPhoneAccount;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

public class CheckVoiceStatusTag extends ZimbraSimpleTag {
	private String mVar;

	public void setVar(String var) { mVar = var; }

	public void doTag() throws JspException, IOException {
		try {
			ZMailbox mbox = getMailbox();
			List<ZPhoneAccount> accounts = mbox.getAllPhoneAccounts();
			Boolean ok = accounts.size() > 0;
			getJspContext().setAttribute(mVar, ok, PageContext.PAGE_SCOPE);
		} catch (ServiceException e) {
			getJspContext().setAttribute(mVar, Boolean.FALSE, PageContext.PAGE_SCOPE);
			throw new JspTagException(e);
		}
	}
}