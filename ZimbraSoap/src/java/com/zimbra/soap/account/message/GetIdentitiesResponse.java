/*
 * 
 */

package com.zimbra.soap.account.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Iterables;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.account.type.Identity;

@XmlRootElement(name="GetIdentitiesResponse")
@XmlType(propOrder = {})
public class GetIdentitiesResponse {

    @XmlElement(name=AccountConstants.E_IDENTITY)
    List<Identity> identities = new ArrayList<Identity>();
    
    public List<Identity> getIdentities() {
        return Collections.unmodifiableList(identities);
    }
    
    public void setIdentities(Iterable<Identity> identities) {
        this.identities.clear();
        if (identities != null) {
            Iterables.addAll(this.identities, identities);
        }
    }
}
