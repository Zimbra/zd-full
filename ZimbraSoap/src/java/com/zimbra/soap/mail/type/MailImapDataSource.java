/*
 * 
 */

package com.zimbra.soap.mail.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.ImapDataSource;

@XmlType(propOrder = {})
public class MailImapDataSource
extends MailDataSource
implements ImapDataSource {
    
    public MailImapDataSource() {
    }

    public MailImapDataSource(ImapDataSource data) {
        super(data);
    }
}
