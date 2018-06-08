/*
 * 
 */

package com.zimbra.cs.mailclient.smtp;

import com.zimbra.cs.mailclient.CommandFailedException;

@SuppressWarnings("serial")
final class InvalidRecipientException extends CommandFailedException {

    private String recipient;

    InvalidRecipientException(String recipient, String serverError) {
        super(SmtpConnection.RCPT, "Invalid recipient " + recipient + ": " + serverError);
        this.recipient = recipient;
    }

    String getRecipient() {
        return recipient;
    }

}
