/*
 * 
 */
package com.zimbra.cs.lmtpserver;

import com.zimbra.cs.account.Account;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Message;

public interface LmtpCallback {

    /**
     * Called after the message is delivered to the given account.
     */
    public void afterDelivery(Account account, Mailbox mbox, String envelopeSender, String recipientEmail, Message newMessage);
}
