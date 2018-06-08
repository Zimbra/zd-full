/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.CalDataSource;

@XmlType(propOrder = {})
public class AccountCalDataSource
extends AccountDataSource
implements CalDataSource {

    public AccountCalDataSource() {
    }
    
    public AccountCalDataSource(CalDataSource data) {
        super(data);
    }
}
