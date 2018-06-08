/*
 * 
 */

package com.zimbra.soap.mail.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Iterables;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.mail.type.MailCalDataSource;
import com.zimbra.soap.mail.type.MailImapDataSource;
import com.zimbra.soap.mail.type.MailPop3DataSource;
import com.zimbra.soap.mail.type.MailRssDataSource;
import com.zimbra.soap.type.DataSource;

@XmlRootElement(name="GetDataSourcesResponse")
@XmlType(propOrder = {})
public class GetDataSourcesResponse {
    
    @XmlElements({
        @XmlElement(name=MailConstants.E_DS_POP3, type=MailPop3DataSource.class),
        @XmlElement(name=MailConstants.E_DS_IMAP, type=MailImapDataSource.class),
        @XmlElement(name=MailConstants.E_DS_RSS, type=MailRssDataSource.class),
        @XmlElement(name=MailConstants.E_DS_CAL, type=MailCalDataSource.class)
    })
    private List<DataSource> dataSources = new ArrayList<DataSource>();
    
    public List<DataSource> getDataSources() { return Collections.unmodifiableList(dataSources); }
    
    public void setDataSources(Iterable<DataSource> dataSources) {
        this.dataSources.clear();
        if (dataSources != null) {
            Iterables.addAll(this.dataSources, dataSources);
        }
    }
}
