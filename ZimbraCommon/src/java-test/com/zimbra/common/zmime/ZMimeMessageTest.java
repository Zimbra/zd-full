/*
 * 
 */
package com.zimbra.common.zmime;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import junit.framework.Assert;

import org.junit.Test;

public class ZMimeMessageTest {
    private class MimeMessageWithId extends ZMimeMessage {
        private final String mMessageId;

        MimeMessageWithId(String messageId) {
            super(Session.getDefaultInstance(new Properties()));
            mMessageId = messageId;
        }

        @Override
        protected void updateMessageID() throws MessagingException {
            setHeader("Message-ID", mMessageId);
        }
    }

    private static String[] HEADERS = {
        "Date: Mon, 18 Jul 2011 11:30:12 -0700",
        "MIME-Version: 1.0",
        "Subject: re: Your Brains",
        "From: DONOTREPLY@example.com",
        "To: otheruser@example.com",
        "Content-Type: text/plain",
        "X-Face: :/"
    };

    @Test
    public void addHeaderLine() throws Exception {
        MimeMessage mm = new MimeMessageWithId("<sample-823745-asd-23432452345@example.com>");
        for (String line : HEADERS) {
            mm.addHeaderLine(line + "\r\n");
        }
        mm.setContent("", mm.getContentType());
        mm.writeTo(System.out);
    }

    @Test
    public void cdisp() throws Exception {
        MimeMessage mm = new MimeMessageWithId("<sample-823745-asd-23432452345@example.com>");
        for (String line : HEADERS) {
            mm.addHeaderLine(line + "\r\n");
        }
        Assert.assertEquals("cdisp unset", null, mm.getDisposition());

        mm.addHeaderLine("Content-Disposition: \r\n");
        Assert.assertEquals("cdisp effectively unset", null, mm.getDisposition());

        mm.setHeader("Content-Disposition", "attachment");
        Assert.assertEquals("cdisp: attachment", "attachment", mm.getDisposition());

        mm.setHeader("Content-Disposition", "inline");
        Assert.assertEquals("cdisp: inline", "inline", mm.getDisposition());

        mm.setHeader("Content-Disposition", "foo");
        Assert.assertEquals("cdisp defaulted", "attachment", mm.getDisposition());
    }
}
