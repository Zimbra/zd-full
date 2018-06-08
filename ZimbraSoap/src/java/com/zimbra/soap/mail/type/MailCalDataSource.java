/*
 * 
 */

package com.zimbra.soap.mail.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.CalDataSource;

@XmlType(propOrder = {})
public class MailCalDataSource
extends MailDataSource
implements CalDataSource {

    public MailCalDataSource() {
    }
    
    public MailCalDataSource(CalDataSource data) {
        super(data);
    }
}
