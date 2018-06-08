/*
 * 
 */

package com.zimbra.soap.type;

import com.zimbra.soap.account.type.AccountCalDataSource;
import com.zimbra.soap.account.type.AccountImapDataSource;
import com.zimbra.soap.account.type.AccountPop3DataSource;
import com.zimbra.soap.account.type.AccountRssDataSource;

public class DataSources {
    
    public static Pop3DataSource newPop3DataSource() {
        return new AccountPop3DataSource();
    }
    
    public static Pop3DataSource newPop3DataSource(Pop3DataSource data) {
        return new AccountPop3DataSource(data);
    }

    public static ImapDataSource newImapDataSource() {
        return new AccountImapDataSource();
    }
    
    public static ImapDataSource newImapDataSource(ImapDataSource data) {
        return new AccountImapDataSource(data);
    }
    
    public static RssDataSource newRssDataSource() {
        return new AccountRssDataSource();
    }
    
    public static RssDataSource newRssDataSource(RssDataSource data) {
        return new AccountRssDataSource(data);
    }
    
    public static CalDataSource newCalDataSource() {
        return new AccountCalDataSource();
    }
    
    public static CalDataSource newCalDataSource(CalDataSource data) {
        return new AccountCalDataSource(data);
    }
}
