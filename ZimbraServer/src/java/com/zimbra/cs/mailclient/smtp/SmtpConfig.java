/*
 * 
 */
package com.zimbra.cs.mailclient.smtp;

import com.google.common.base.Objects;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.mailclient.MailConfig;

/**
 * SMTP client configuration.
 */
public final class SmtpConfig extends MailConfig {
    public static final String PROTOCOL = "smtp";
    public static final int DEFAULT_PORT = 25;
    public static final int DEFAULT_SSL_PORT = 465;

    private String domain;
    private boolean allowPartialSend;

    public SmtpConfig(String host, int port, String domain) {
        super(ZimbraLog.smtp, host);
        setPort(port);
        setDomain(domain);
    }

    public SmtpConfig(String host) {
        super(ZimbraLog.smtp, host);
        setPort(DEFAULT_PORT);
    }

    public SmtpConfig() {
        super(ZimbraLog.smtp);
    }

    @Override
    public String getProtocol() {
        return PROTOCOL;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return Objects.firstNonNull(domain, "localhost");
    }

    public void setAllowPartialSend(boolean allow) {
        this.allowPartialSend = allow;
    }

    public boolean isPartialSendAllowed() {
        return allowPartialSend;
    }

}
