/*
 * 
 */

package com.zimbra.soap.mail.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.RssDataSource;

@XmlType(propOrder = {})
public class MailRssDataSource
extends MailDataSource
implements RssDataSource {

    public MailRssDataSource() {
    }
    
    public MailRssDataSource(RssDataSource data) {
        super(data);
    }
}
