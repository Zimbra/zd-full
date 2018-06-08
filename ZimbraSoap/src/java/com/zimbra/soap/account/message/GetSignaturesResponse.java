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

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.account.type.Signature;

@XmlRootElement(name="GetSignaturesResponse")
@XmlType(propOrder = {})
public class GetSignaturesResponse {

    @XmlElement(name=AccountConstants.E_SIGNATURE)
    private List<Signature> signatures = new ArrayList<Signature>();
    
    public List<Signature> getSignatures() { return Collections.unmodifiableList(signatures); }
    
    public void setSignatures(Iterable<Signature> signatures) {
        this.signatures.clear();
    }
}
