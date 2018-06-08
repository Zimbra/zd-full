/*
 * 
 */
package com.zimbra.cs.mailclient.imap;

public interface ResponseHandler {
    void handleResponse(ImapResponse res) throws Exception;
}
