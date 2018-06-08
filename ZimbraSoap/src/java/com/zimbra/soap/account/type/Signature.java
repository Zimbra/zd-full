/*
 * 
 */

package com.zimbra.soap.account.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.collect.Iterables;
import com.zimbra.common.soap.AccountConstants;

/*
     <signature name={signature-name} id="...">
       <a name="{name}">{value}</a>
       ...
       <a name="{name}">{value}</a>
     </signature>*

 */
@XmlType(propOrder = {})
public class Signature {

    @XmlAttribute private String id;
    @XmlAttribute private String name;
    @XmlElement(name=AccountConstants.E_A) private List<Attr> attrs = new ArrayList<Attr>();
    @XmlElement(name=AccountConstants.E_CONTENT) private List<SignatureContent> contentList =
        new ArrayList<SignatureContent>();
    
    public Signature() {
    }
    
    public Signature(Signature sig) {
        id = sig.getId();
        name = sig.getName();
        contentList.addAll(sig.getContent());
    }
    
    public Signature(String id, String name, String content, String contentType) {
        this.id = id;
        this.name = name;
        if (content != null) {
            this.contentList.add(new SignatureContent(content, contentType));
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Attr> getAttrs() {
        return Collections.unmodifiableList(attrs);
    }
    
    public void setAttrs(Iterable<Attr> attrs) {
        this.attrs.clear();
        if (attrs != null) {
            Iterables.addAll(this.attrs, attrs);
        }
    }
    
    public List<SignatureContent> getContent() {
        return Collections.unmodifiableList(contentList);
    }
    
    public void addContent(SignatureContent content) {
        this.contentList.add(content);
    }
    
    public void setContent(Iterable<SignatureContent> content) {
        this.contentList.clear();
        if (content != null) {
            Iterables.addAll(this.contentList, content);
        }
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
