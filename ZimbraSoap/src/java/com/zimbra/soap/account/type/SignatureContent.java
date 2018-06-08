/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.zimbra.common.soap.AccountConstants;

@XmlType(propOrder = {})
public class SignatureContent {

    @XmlAttribute(name=AccountConstants.A_TYPE) private String contentType;
    @XmlValue private String content;
    
    public SignatureContent() {
    }
    
    public SignatureContent(String content, String contentType) {
        this.content = content;
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
}
