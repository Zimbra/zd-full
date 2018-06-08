/*
 * 
 */
package com.zimbra.cs.mailclient.imap;

public interface DataHandler {
    Object handleData(ImapData data) throws Exception;
}
