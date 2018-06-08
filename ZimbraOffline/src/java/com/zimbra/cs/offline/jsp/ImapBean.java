/*
 * 
 */
package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;

public class ImapBean extends XmailBean {

    protected String oauthURL = "";
    protected String oauthVerifier = "";
    protected String oauthTmpId = "";

    public ImapBean() {
        port = "143";
        type = DataSource.Type.imap.toString();
    }

    public boolean isFolderSyncSupported() {
        return true;
    }

    public String getOauthURL() {
        return oauthURL;
    }

    public void setOauthURL(String oauthURL) {
        this.oauthURL = oauthURL;
    }

    public String getOauthVerifier() {
        return oauthVerifier;
    }

    public void setOauthVerifier(String oauthVerifier) {
        this.oauthVerifier = oauthVerifier;
    }

    public String getOauthTmpId() {
        return oauthTmpId;
    }

    public void setOauthTmpId(String oauthTmpId) {
        this.oauthTmpId = oauthTmpId;
    }
}
