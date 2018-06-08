/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/*
       [<session [id="{returned-from-server-in-last-response}" [seq="{highest_notification_received}"]]/>]
 */
@XmlType(propOrder = {})
public class Session {

    @XmlElement private String id;
    @XmlElement private Long seq;
    
    public Session() {
    }
    
    public Session(String id, Long seq) {
        setId(id);
        setSeq(seq);
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getSeq() { return seq; }
    public void setSeq(Long seq) { this.seq = seq; }
}
