/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.DataSource;
import com.zimbra.soap.type.ImapDataSource;

@XmlType(propOrder= {})
public class AccountImapDataSource
extends AccountDataSource
implements ImapDataSource {
    
    public AccountImapDataSource() {
    }

    public AccountImapDataSource(DataSource data) {
        super(data);
    }
}
