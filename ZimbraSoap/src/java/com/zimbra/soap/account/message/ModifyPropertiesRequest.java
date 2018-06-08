/*
 * 
 */

package com.zimbra.soap.account.message;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.account.type.Prop;


/**
<ModifyPropertiesRequest>
    <prop zimlet="{zimlet-name}" name="{name}">{value}</prop>
    ...
    <prop zimlet="{zimlet-name}" name="{name}">{value}</prop>
</ModifyPropertiesRequest>
 */
@XmlRootElement(name="ModifyPropertiesRequest")
@XmlType(propOrder = {})
public class ModifyPropertiesRequest {
    @XmlElements({
        @XmlElement(name=AccountConstants.E_PROPERTY, type=Prop.class)
    })
    
    private List<Prop> props = new ArrayList<Prop>();

    public List<Prop> getProps() {
        return props; 
    }
    
    public void setProps(List<Prop> props) {
        this.props = props;
    }
}
