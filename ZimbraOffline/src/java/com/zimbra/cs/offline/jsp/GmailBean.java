/*
 * 
 */
package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;

public class GmailBean extends ImapBean {
    public static final String Domain = "gmail.com";

    public GmailBean() {}

    @Override
    protected void doRequest() {
        domain = Domain;
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email) && email.indexOf('@') < 0)
                email += '@' + domain;
            username = email;
        }
        host = OfflineLC.zdesktop_gmail_imap_host.value();
        connectionType = DataSource.ConnectionType.ssl;
        port = "993";

        smtpHost = OfflineLC.zdesktop_gmail_smtp_host.value();
        smtpPort = "465";
        isSmtpSsl = true;
        isSmtpAuth = true;
        smtpUsername = email;
        smtpPassword = password;
        super.doRequest();
    }

    public boolean isCalendarSyncSupported() {
        return true;
    }

    public boolean isContactSyncSupported() {
        //TODO: Disabling contact sync until OAuth implementation in place. 
        return false;
    }

    public boolean isServerConfigSupported() {
        return false;
    }

    public boolean isSmtpConfigSupported() {
        return false;
    }

    public boolean isUsernameRequired() {
        return false;
    }
}
