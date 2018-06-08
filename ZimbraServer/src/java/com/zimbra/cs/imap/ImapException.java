/*
 * 
 */
package com.zimbra.cs.imap;

/**
 * IMAP base exception.
 *
 * @author ysasaki
 */
abstract class ImapException extends Exception {
    private static final long serialVersionUID = -7723826215470186860L;

    ImapException() {
    }

    ImapException(String message) {
        super(message);
    }

    ImapException(String message, Throwable cause) {
        super(message, cause);
    }
}
