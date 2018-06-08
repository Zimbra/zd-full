/*
 * 
 */
package com.zimbra.cs.offline.jsp;

import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.offline.OfflineLC;

public class YmailBean extends ImapBean {

    public static final String Domain = "yahoo.com";
    public static final String Domain_JP = "yahoo.co.jp";

    public YmailBean() {
    }

    @Override
    protected void doRequest() {
        domain = email.endsWith("yahoo.co.jp") ? Domain_JP : Domain;
        if (verb != null && (verb.isAdd() || verb.isModify())) {
            if (!isEmpty(email)) {
                if (email.indexOf('@') < 0)
                    email += '@' + domain;

                int atSign = email.indexOf("@yahoo.");

                if (atSign > 0) // username of yahoo.* email is without @domain
                    username = email.substring(0, atSign);
                else
                    username = email;
            }
        }

        host = email.endsWith("@yahoo.co.jp") ? OfflineLC.zdesktop_yahoo_imap_host_jp.value() : OfflineLC.zdesktop_yahoo_imap_host.value();
        connectionType = DataSource.ConnectionType.ssl;
        port = OfflineLC.zdesktop_yahoo_imap_ssl_port.value();
        smtpHost = email.endsWith("@yahoo.co.jp") ? OfflineLC.zdesktop_yahoo_smtp_host_jp.value() : OfflineLC.zdesktop_yahoo_smtp_host.value();
        smtpPort = OfflineLC.zdesktop_yahoo_smtp_ssl_port.value();
        isSmtpSsl = true;
        isSmtpAuth = true;
        smtpUsername = email;
        smtpPassword = password;
        super.doRequest();
    }

    public boolean isCalendarSyncSupported() {
        return true;
    }

    //Contact sync is disabled due to unsupported Yahoo mail API's.
    public boolean isContactSyncSupported() {
        //TODO: return true once contact sync implementation is in place.
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
    
    public boolean isYcontactTokenSaved() {
        return isYcontactTokenSaved; 
    }

    public void setYcontactTokenSaved(boolean isYcontactTokenSaved) {
        this.isYcontactTokenSaved = isYcontactTokenSaved;
    }
}
