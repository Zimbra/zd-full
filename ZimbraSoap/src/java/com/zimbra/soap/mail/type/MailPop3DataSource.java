/*
 * 
 */

package com.zimbra.soap.mail.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.MailConstants;
import com.zimbra.soap.type.Pop3DataSource;

@XmlType(propOrder = {})
public class MailPop3DataSource
extends MailDataSource
implements Pop3DataSource {

    @XmlAttribute(name=MailConstants.A_DS_LEAVE_ON_SERVER)
    private Boolean leaveOnServer;

    public MailPop3DataSource() {
    }
    
    public MailPop3DataSource(Pop3DataSource data) {
        super(data);
        leaveOnServer = data.isLeaveOnServer();
    }
    
    @Override
    public Boolean isLeaveOnServer() {
        return leaveOnServer;
    }
    
    public void setLeaveOnServer(Boolean leaveOnServer) {
        this.leaveOnServer = leaveOnServer;
    }
}
