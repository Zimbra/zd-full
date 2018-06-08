/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

@SuppressWarnings("serial")
public class CommandCallbackException extends Exception {

    public CommandCallbackException(Throwable t) {
        super(t);
    }
}
