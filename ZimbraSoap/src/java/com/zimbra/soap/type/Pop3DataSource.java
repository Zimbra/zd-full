/*
 * 
 */

package com.zimbra.soap.type;

public interface Pop3DataSource
extends DataSource {
    
    public Boolean isLeaveOnServer();
    
    public void setLeaveOnServer(Boolean leaveOnServer);
}
