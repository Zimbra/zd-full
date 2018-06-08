/*
 * 
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.ZJspSession;
import com.zimbra.cs.zclient.ZChangePasswordResult;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ChangePasswordTag extends ZimbraSimpleTag {

    private String mUsername;
    private String mPassword;
    private String mNewPassword;
    private String mUrl = null;
    private boolean mSecure = ZJspSession.isProtocolModeHttps();
    private boolean mRememberMe;

    public void setUsername(String username) { this.mUsername = username; }

    public void setPassword(String password) { this.mPassword = password; }

    public void setNewpassword(String password) { this.mNewPassword = password; }

    public void setUrl(String url) { this.mUrl = url; }

    public void setSecure(boolean secure) { this.mSecure = secure; }

    public void setRememberme(boolean rememberMe) { this.mRememberMe = rememberMe; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            PageContext pageContext = (PageContext) jctxt;
            ZMailbox.Options options = new ZMailbox.Options();
            options.setAccount(mUsername);
            options.setPassword(mPassword);
            options.setNewPassword(mNewPassword);
            options.setUri(mUrl == null ? ZJspSession.getSoapURL(pageContext): mUrl);
            ZChangePasswordResult cpr = ZMailbox.changePassword(options);

            LoginTag.setCookie((HttpServletResponse)pageContext.getResponse(),
                    cpr.getAuthToken(),
                    mSecure,
                    mRememberMe,
                    cpr.getExpires());
 
        } catch (ServiceException e) {

            throw new JspTagException(e.getMessage(), e);
        }
    }
}
