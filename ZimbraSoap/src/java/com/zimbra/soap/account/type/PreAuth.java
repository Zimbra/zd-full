/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;


/*
<preauth timestamp="{timestamp}" expires="{expires}">{computed-preauth-value}</preauth>
 */
public class PreAuth {

    @XmlAttribute(required=true) private long timestamp;
    @XmlAttribute private Long expiresTimestamp;
    @XmlValue private String value;
    
    public long getTimestamp() { return timestamp; }
    public PreAuth setTimestamp(long timestamp) { this.timestamp = timestamp; return this; }
    
    public Long getExpiresTimestamp() { return expiresTimestamp; }
    public PreAuth setExpiresTimestamp(Long timestamp) { this.expiresTimestamp = timestamp; return this; }
    
    public String getValue() { return value; }
    public PreAuth setValue(String value) { this.value = value; return this; }
}
