/*
 * 
 */
package com.zimbra.cs.mailclient.imap;

import com.zimbra.common.util.Log;
import com.zimbra.cs.mailclient.MailOutputStream;

import java.io.OutputStream;

/**
 * An output stream for writing IMAP request data.
 */
public class ImapOutputStream extends MailOutputStream {
    public ImapOutputStream(OutputStream os) {
        super(os);
    }

    public ImapOutputStream(OutputStream os, Log log) {
        super(os, log);
    }
}
