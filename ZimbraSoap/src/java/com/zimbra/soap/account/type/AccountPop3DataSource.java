/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.type.Pop3DataSource;

@XmlType(propOrder = {})
public class AccountPop3DataSource
extends AccountDataSource
implements Pop3DataSource {

    @XmlAttribute(name=MailConstants.A_DS_LEAVE_ON_SERVER)
    private Boolean leaveOnServer;
    
    public AccountPop3DataSource() {
    }
    
    public AccountPop3DataSource(Pop3DataSource data) {
        super(data);
        leaveOnServer = data.isLeaveOnServer();
    }
    
    @Override
    public Boolean isLeaveOnServer() {
        return leaveOnServer;
    }
    
    @Override
    public void setLeaveOnServer(Boolean leaveOnServer) {
        this.leaveOnServer = leaveOnServer;
    }
}
