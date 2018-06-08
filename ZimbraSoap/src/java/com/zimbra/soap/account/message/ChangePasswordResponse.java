/*
 * 
 */

package com.zimbra.soap.account.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/*
<ChangePasswordResponse>
   <authToken>...</authToken>
   <lifetime>...</lifetime>
<ChangePasswordResponse/>
*/
@XmlRootElement(name="ChangePasswordResponse")
@XmlType(propOrder = {})
public class ChangePasswordResponse {

    @XmlElement(required = true) String authToken;
    @XmlElement(required = true) long lifetime;
    
    public ChangePasswordResponse() {
    }
    
    public String getAuthToken() { return authToken; }
    public long getLifetime() { return lifetime; }
    
    public ChangePasswordResponse setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }
    
    public ChangePasswordResponse setLifetime(long lifetime) {
        this.lifetime = lifetime;
        return this;
    }
}
