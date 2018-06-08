/*
 * 
 */

package com.zimbra.soap.account.type;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <cos name="cos-name" id="cos-id"/>
 */
@XmlType(propOrder = {})
public class Cos {
    @XmlAttribute private String name;
    @XmlAttribute private String id;
    
    public Cos() {
    }
    
    public String getName() { return name; }
    public String getId() { return id; }
    
    public Cos setName(String name) {
        this.name = name;
        return this;
    }
    
    public Cos setId(String id) {
        this.id = id;
        return this;
    }
}
