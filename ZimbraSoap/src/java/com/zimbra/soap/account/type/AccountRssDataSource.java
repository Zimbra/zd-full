/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlType;

import com.zimbra.soap.type.RssDataSource;

@XmlType(propOrder= {})
public class AccountRssDataSource
extends AccountDataSource
implements RssDataSource {

    public AccountRssDataSource() {
    }
    
    public AccountRssDataSource(RssDataSource data) {
        super(data);
    }
}
