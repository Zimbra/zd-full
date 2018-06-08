/*
 * 
 */
package com.zimbra.cs.mailclient;

/**
 * Indicates that a parsing error occurred while reading a mail protocol
 * response.
 */
public class ParseException extends MailException {
    /**
     * Creates a new <tt>ParseException</tt> with a <tt>null</tt> detail
     * message.
     */
    public ParseException() {}

    /**
     * Creates a new <tt>ParseException</tt> with the specified detail message.
     *
     * @param msg the detail message, or <tt>null</tt> if none
     */
    public ParseException(String msg) {
        super(msg);
    }
}
